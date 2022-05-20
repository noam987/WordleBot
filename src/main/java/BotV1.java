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
import java.util.Random;


public class BotV1 extends ListenerAdapter {
    private static ArrayList<String> wordListHard;
    private static ArrayList<String> wordList;
    private Hashtable<User, Solver> solvers;
    private Hashtable<User, ArrayList<Solver>> quordles;

    public BotV1() {
        this.solvers = new Hashtable<>();
        this.quordles = new Hashtable<>();
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
        try {
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
                guess = guess.toLowerCase();

                int[] scores = solver.compare(guess);

                solver.results[solver.guesses] = scores;
                solver.wordsGuessed[solver.guesses] = guess;
                solver.guesses++;
                String output = "";
                solver.setLetterStatus(guess, scores);
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
            if (message.getContentRaw().equalsIgnoreCase("!hint")) {
                if (!solvers.containsKey(author)) {
                    channel.sendMessage("You don't have a game in progress").queue();
                    return;
                }
                Solver solver = solvers.get(author);
                String hint = solver.returnValidWords().get(new Random().nextInt(solver.returnValidWords().size()));
                channel.sendMessage("One possible word is " + hint).queue();
                return;
            }
            if (message.getContentRaw().startsWith("!multiwordle")) {
                ArrayList<Solver> games = new ArrayList<>();
                int number;
                try {
                    number = Integer.parseInt(message.getContentRaw().split(" ")[1]);
                } catch (Exception e) {
                    channel.sendMessage("Please enter a number").queue();
                    return;
                }
                for (int i = 0; i < number; i++) {
                    games.add(new Solver(wordList));

                }

                if (quordles.containsKey(author)) {
                    quordles.replace(author, games);
                } else {
                    quordles.put(author, games);
                }
                channel.sendMessage(author.getAsMention() + "Please Guess a 5 Letter word using \"!multiguess word\"").queue();
            }
            if (message.getContentRaw().startsWith("!multiguess")) {
                if (!quordles.containsKey(author)) {
                    channel.sendMessage("Please start a multi game first using !multiwordle X").queue();
                    return;
                }
                for (Solver solver : quordles.get(author)) {


                    String guess = message.getContentRaw().split(" ")[1];
                    if (guess.length() != 5) {
                        channel.sendMessage("Please guess a 5 letter word").queue();
                        return;
                    }
                    guess = guess.toLowerCase();

                    channel.sendMessage("Word " + (quordles.get(author).indexOf(solver) + 1)).queue();
                    int[] scores = solver.compare(guess);

                    solver.results[solver.guesses] = scores;
                    solver.wordsGuessed[solver.guesses] = guess;
                    solver.guesses++;
                    String output = "";
                    solver.setLetterStatus(guess, scores);
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
        } catch (Exception e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
            return;
        }
    }
}
