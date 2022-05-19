import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Solver {
    public String target;
    public ArrayList<String> wordList;
    public HashMap<Character, int[]> letterStatus;
    public int[][] results = new int[6][5];
    public int guesses = 0;
    public String[] wordsGuessed;
    public Solver(ArrayList<String> wordList) {
        letterStatus = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            letterStatus.put((char) ('a' + i), new int[]{0, 0, 0, 0, 0, 0});
        }
        this.wordList = new ArrayList<>();
        for (String word:wordList) {
            this.wordList.add(word);
        }

        target = wordList.get(new Random().nextInt(wordList.size() - 1));
        this.guesses = 0;
        this.wordsGuessed = new String[6];
        this.results = new int[6][5];
        System.out.println(target);
    }

    public Solver(String target, ArrayList<String> wordList) {
        letterStatus = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            letterStatus.put((char) ('a' + i), new int[]{0, 0, 0, 0, 0, 0});
        }
        this.wordList = new ArrayList<>();
        for (String word:wordList) {
            this.wordList.add(word);
        }
        this.target = target;
    }

    public int[] compare(String word) {
        int[] score = new int[5];
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == target.charAt(i)) {
                score[i] = 2;
            } else if (target.contains(word.charAt(i) + "")) {
                boolean first = true;
                String temp = "";
                for (int j = 0; j < i; j++) {
                    if (word.charAt(i) == word.charAt(j)) {
                        first = false;
                        temp = target.replaceFirst(target.charAt(j)+"", " ");
                    }
                }
                if (first || temp.contains(word.charAt(i)+"")) {
                    score[i] = 1;
                }
            } else {
                score[i] = 0;
            }
        }
        return score;
    }

    public void setLetterStatus(String word, int[] scores) {
        for (int i = 0; i < 5; i++) {
            char letter = word.charAt(i);
            if (scores[i] == 0) {
                int current = letterStatus.get(letter)[5];
                if (current == 0) {
                    letterStatus.replace(letter, new int[]{-1, -1, -1, -1, -1, -1}
                    );
                }
                else{
                    int[] newStatus = letterStatus.get(letter);
                    if (newStatus[5] > 0) {
                        newStatus[i] = -1;
                        newStatus[5] = 1;
                        letterStatus.replace(letter, newStatus);
                    }
                }
            }
            if (scores[i] == 2) {
                int[] newStatus = letterStatus.get(letter);
                newStatus[i] = 1;
                newStatus[5] = 1;
                letterStatus.replace(letter, newStatus);
            }
            if (scores[i] == 1) {
                int[] status = letterStatus.get(letter);
                status[i] = -1;
                status[5] = 1;
                letterStatus.replace(letter, status);
            }
        }
    }

    public boolean isValidWord(String word) {
        for (int i = 0; i < 26; i++) {
            char letter = (char) (i + 'a');
            int[] letterScore = letterStatus.get(letter);
            if (letterScore[5] == -1) {
                if (word.contains(letter + "")) {
                    return false;
                }
            } else if (letterScore[5] == 1) {
                if (!word.contains(letter + "")) {
                    return false;
                }
                for (int j = 0; j < 5; j++) {
                    char currentLetter = word.charAt(j);
                    if (letter == currentLetter) {
                        if (letterScore[j] == -1) {
                            return false;
                        }
                    }
                    if (letterScore[j] == 1) {
                        if (letter != currentLetter) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void removeInvalidWords() {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (!isValidWord(wordList.get(i))) {
                wordList.remove(i);
            }
        }
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public ArrayList<String> returnValidWords() {
        ArrayList<String> output = new ArrayList<>();
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (isValidWord(wordList.get(i))) {
                output.add(wordList.get(i));
            }
        }
        return output;
    }


    public String listRemainingWords() {
        String output = "";
        if (wordList.size() > 200) {
            output += "Too many words to list\n";
        }

        for (int i = 0; i < wordList.size() && i < 200; i++) {
            output += wordList.get(i);
            if (i % 15 == 14) {
                output += "\n";
            } else {
                output += ", ";

            }
        }
        return output;
    }


}
