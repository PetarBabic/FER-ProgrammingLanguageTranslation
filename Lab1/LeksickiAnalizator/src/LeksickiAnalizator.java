import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

class LexAnalyzer {
    String input[];
    StringBuilder output = new StringBuilder();

    public LexAnalyzer(String input) {
        this.input = input.split("\n");
    }

    void start() {
        int i = 0;

        while(true) {
            startState(input[i], i);

            i++;
            if(i == input.length)
                break;
        }
    }

    void startState(String line, int counter) {
        counter++;
        line = line.trim();

        if(line.startsWith("//") || line.length() == 0)
            return;
        else
            variables(line, counter);
    }

    void variables(String line, int counter) {
        StringBuilder var = new StringBuilder();
        line.replaceAll("\\s+","");

        for (int i = 0; i < line.length(); i++) {
            if(line.charAt(i) >= 'a' && line.charAt(i) <= 'z' || line.charAt(i) >= 'A' && line.charAt(i) <= 'Z') {
                if(var.length() > 0 && var.charAt(0) >= '0' && var.charAt(0) <= '9') {
                    output.append(var.toString() + "\n");
                    var = new StringBuilder();
                }
                var.append(line.charAt(i));
                if(var.length() == 1)
                    output.append("IDN " + counter + " ");
            }
            else if(line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                var.append(line.charAt(i));
                if(var.length() == 1)
                    output.append("BROJ " + counter + " ");
            }
            else if(line.length() > i + 1 && line.charAt(i) == '/' && line.charAt(i + 1) == '/')
                return;
            else {
                if(var.length() != 0) {
                    if(var.toString().compareTo("az") == 0)
                        output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_AZ " + counter + " ");
                    else if(var.toString().compareTo("za") == 0)
                        output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_ZA " + counter + " ");
                    else if(var.toString().compareTo("od") == 0)
                        output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_OD " + counter + " ");
                    else if(var.toString().compareTo("do") == 0)
                        output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_DO " + counter + " ");
                    output.append(var + "\n");
                }
                var = new StringBuilder();
                operator(line.charAt(i), counter);
            }
        }
        if(output.length() > 0) {
            if(var.toString().compareTo("az") == 0)
                output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_AZ " + counter + " ");
            else if(var.toString().compareTo("za") == 0)
                output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_ZA " + counter + " ");
            else if(var.toString().compareTo("od") == 0)
                output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_OD " + counter + " ");
            else if(var.toString().compareTo("do") == 0)
                output.replace(output.length() - (5 +  (int)Math.log10(counter) + 1), output.length(), "KR_DO " + counter + " ");

            if (output.charAt(output.length() - 1) != '\n')
                output.append(var.toString() + "\n");
        }
    }
    void operator(char op, int counter) {
        switch (op) {
            case '=':
                output.append("OP_PRIDRUZI " + counter + " =\n");
                break;
            case '*':
                output.append("OP_PUTA " + counter + " *\n");
                break;
            case '+':
                output.append("OP_PLUS " + counter + " +\n");
                break;
            case '-':
                output.append("OP_MINUS " + counter + " -\n");
                break;
            case '/':
                output.append("OP_DIJELI " + counter + " /\n");
                break;
            case '(':
                output.append("L_ZAGRADA " + counter + " (\n");
                break;
            case ')':
                output.append("D_ZAGRADA " + counter + " )\n");
                break;
        }
    }
}

public class LeksickiAnalizator {
    public static void main(String[] args) {
//        testOneFile("integration/test30/test.in");
//        tester();
        userInput();
    }

    static void tester() {
        String path = "integration/test";
        String folder = "";

        int[] counter = {0, 0};

        for(Integer i = 0; i <= 111; i++) {
            try {
                path = "integration/test";
                if (i < 10)
                    folder = "0" + i;
                else
                    folder = i.toString();

                path += folder;
                String pathInput = path + "/test.in";
                String pathOutput = path + "/test.out";

                StringBuilder input = new StringBuilder();
                try {
                    Scanner scanner = new Scanner(new File(pathInput));

                    while (scanner.hasNextLine()) {
                        input.append(scanner.nextLine()).append("\n");
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                LexAnalyzer analyze = new LexAnalyzer(input.toString());
                analyze.start();

                input = new StringBuilder();
                try {
                    Scanner scanner = new Scanner(new File(pathOutput));

                    while (scanner.hasNextLine()) {
                        input.append(scanner.nextLine()).append("\n");
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String myOutput = analyze.output.toString().trim();
                String correct = input.toString().strip().trim();

                System.out.print(i + "\t");
                if (correct.compareTo(myOutput) == 0) {
                    System.out.println("\u001B[32m" + "Correct " + "\u001B[0m");
                    counter[0]++;
                }
                else {
                    System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                    System.out.println("Correct: " + correct);
                    System.out.println("MyOutput:" + myOutput);
                    counter[1]++;
                }
            } catch (Exception e) {
                System.out.print(i + "\t");
                System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                System.out.println(e);
                counter[1]++;
            }
        }
        System.out.println("\u001B[32m" + "Correct: " + counter[0] + "\u001B[0m");
        System.out.println("\u001B[31m" + "Wrong: " + counter[1] + "\u001B[0m");
    }
    static void testOneFile(String path) {
        StringBuilder input = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(path));

            while (scanner.hasNextLine()) {
                input.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LexAnalyzer analyze = new LexAnalyzer(input.toString());
        analyze.start();
        System.out.println(analyze.output.toString());

        for (String s : analyze.input) {
            System.out.println(s);
        }
        /*
        System.out.println(machine);
        machine.removeUnreachableStates(new ArrayList<>(Arrays.asList(machine.start)));
        System.out.println(machine);
        */
        //System.out.println("\n" + machine.output());
    }
    static void userInput() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder inputBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            inputBuilder.append(line).append("\n");
        }

        String input = inputBuilder.toString();
        scanner.close();

        LexAnalyzer machine = new LexAnalyzer(input);

        machine.start();

        System.out.println(machine.output.toString());
    }
}