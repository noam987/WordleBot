import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


public class BotV1 extends ListenerAdapter {
    private static ArrayList<String> wordListHard;
    private static ArrayList<String> wordList;
    private Hashtable <User, Solver> solvers;
    public BotV1() {
        this.solvers = new Hashtable<>();
    }
    public static void main(String[] args) throws InterruptedException, LoginException, IOException {
        BufferedReader tokenReader = new BufferedReader(new FileReader("token.secret"));
        String token = tokenReader.readLine();
        JDA jda = JDABuilder.createDefault(token).setActivity(Activity.playing("!wordle or !wordle hard")).build();
        jda.addEventListener(new BotV1());
        jda.awaitReady();
        ArrayList<String> words = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader("word_list.txt"));
        String line = bf.readLine();
        while (line != null && !line.isEmpty()) {
            words.add(line);
            line = bf.readLine();
        }
        bf.close();
        wordListHard = words;
        bf = new BufferedReader(new FileReader("OfficialAnswers.txt"));
        words = new ArrayList<>();
        line = bf.readLine();
        while (line != null && !line.isEmpty()) {
            words.add(line);
            line = bf.readLine();
        }
        bf.close();
        wordList = words;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = message.getAuthor();
        MessageChannel channel = event.getChannel();

        if (message.getContentRaw().equals("!ping")) {
            long time = System.currentTimeMillis();
            channel.sendMessage(":yellow_square: " + message.getAuthor().getName()).queue();

        }
        if (message.getContentRaw().equalsIgnoreCase("!wordle")) {
            if (solvers.containsKey(author)) {
                solvers.replace(author, new Solver(wordList));
            } else {
                solvers.put(author, new Solver(wordList));
            }
            channel.sendMessage(author.getAsMention() + "Please Guess a 5 Letter word using \"!guess word\"").queue();
        }
        if (message.getContentRaw().equalsIgnoreCase("!wordle hard")) {
            if (solvers.containsKey(author)) {
                solvers.replace(author, new Solver(wordListHard));
            } else {
                solvers.put(author, new Solver(wordListHard));
            }
            channel.sendMessage(author.getAsMention() + "Please Guess a 5 Letter word using \"!guess word\"").queue();
        }
        if (message.getContentRaw().startsWith("!guess")) {
            if (!solvers.containsKey(author)) {
                channel.sendMessage("Please start a wordle game first using !wordle").queue();
                return;
            }
            Solver solver = solvers.get(author);
            String guess = message.getContentRaw().split(" ")[1];
            if (guess.length() != 5) {
                channel.sendMessage("Please guess a 5 letter word").queue();
                return;
            }
            int[] scores = solver.compare(guess);

            solver.results[solver.guesses] = scores;
            solver.wordsGuessed[solver.guesses] = guess;
            solver.guesses++;
            String output = "";
            for (int i = 0; i < solver.guesses; i++) {
                for (int j = 0; j < 5; j++) {
                    switch (solver.results[i][j]) {
                        case 0:
                            output = output + ":black_large_square:";
                            break;
                        case 1:
                            output = output + ":yellow_square:";
                            break;
                        case 2:
                            output = output + ":green_square:";
                            break;
                    }
                }
                output = output + " " + solver.wordsGuessed[i] + "\n";
            }
            channel.sendMessage(output).queue();
            if (solver.target.equals(guess)) {
                channel.sendMessage("You have guessed the word in " + solver.guesses + " guesses. type !wordle to play again").queue();
                solvers.remove(author);
                return;
            }
            if (solver.guesses == 6) {
                channel.sendMessage("You have ran out of guesses. The word was ||" + solver.target + "||").queue();
                solvers.remove(author);
            }
        }
    }
}
