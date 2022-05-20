import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
public class game {

//    public static void main(String[] args) throws Exception {
//        System.out.println(System.currentTimeMillis());
//        ArrayList<String> words = new ArrayList<>();
//        BufferedReader bf = new BufferedReader(new FileReader("word_list.txt"));
//        String line = bf.readLine();
//        while (line != null && !line.isEmpty()) {
//            words.add(line);
//            line = bf.readLine();
//        }
//        Solver solver = new Solver(words);
//        System.out.println(System.currentTimeMillis());
//        Scanner scanner = new Scanner(System.in);
//        String answer = "";
//        do {
//
//
//            System.out.println("Do you want to (p)lay, or (h)elp solving");
//            answer = scanner.next();
//        } while (!(answer.equals("p") || answer.equals("h")));
//        boolean help = answer.equals("h");
//        while (true) {
//            System.out.println("Make a guess");
//            System.out.println("possible words remaining: " + solver.wordList.size());
//            String guess = scanner.next();
//            System.out.println(System.currentTimeMillis());
//            solver.isValidWord(guess);
//            int[] scores;
//            if (help){
//                scores = wordScoreInput(scanner);
//            }
//            else {
//                scores = solver.compare(guess);
//            }
//            for (int i = 0; i < 5; i++) {
//                System.out.print(guess.charAt(i) + ": " + scores[i] + ", ");
//            }
//            System.out.println();
//            solver.setLetterStatus(guess, scores);
//            for (int i = 0; i < 26; i++) {
//                int[] status = solver.letterStatus.get((char) (i + 'a'));
//                System.out.print((char) ('a' + i) + " = ");
//                System.out.print(status[0] + ", " + status[1] + ", ");
//                System.out.println(status[2] + ", "
//                        + status[3] + ", " + status[4]+": " + status[5]);
//            }
//            solver.removeInvalidWords();
//            System.out.println(solver.listRemainingWords());
//            System.out.println(System.currentTimeMillis());
//
//        }
//
//    }

    public static int[] wordScoreInput(Scanner scanner) {
        int[] score = new int[5];
        for (int i = 0; i < 5; i++) {
            System.out.println("Enter the result of  the first letter. \n" +
                    "0 for not in word, 1 for in word but wrong spot, 2 for right letter in the right spot");
            score[i] = scanner.nextInt();
        }
        return score;
    }

}
