import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class SemAnalyzer {
    String input[];
    StringBuilder output = new StringBuilder();

    public SemAnalyzer(String input) {
        this.input = input.split("\n");
    }
    
    String start() {
        // Map =
        //      Key = Variable name
        //      Values = Infinite Pairs of (First Line, Depth of ZA blocks)
        Map<String, List<Integer>> varMap = new HashMap<>();
        String varName;
        int varLine, firstLine, zaCounter = 0;
        Boolean za = false;

        for(int i = 0; i < input.length; i++) {
            za = false;

            if(input[i].trim().split(" ")[0].compareTo("KR_AZ") == 0) {
                int tmpZa = zaCounter;
                Set<String> keys = varMap.keySet();

                keys.forEach(v -> {
                    List<Integer> currentList = varMap.get(v);
                    for(int j = 0; j < currentList.size() - 1; j += 2) {
                        if(tmpZa == currentList.get(++j)) {
                            varMap.get(v).set(j, -1);
                            varMap.get(v).set(--j, -1);
                        }
                        else --j;
                    }
                });

                for (String key : keys) {
                    varMap.get(key).removeIf(a -> a == -1);
                }

                zaCounter--;
            }
            else if(input[i].trim().split(" ")[0].compareTo("<naredba_pridruzivanja>") == 0 ||
                    input[i].trim().split(" ")[0].compareTo("<za_petlja>") == 0) {
                if(input[i].trim().split(" ")[0].compareTo("<naredba_pridruzivanja>") == 0)
                    i++;
                else {
                    i += 2;
                    zaCounter++;
                    za = true;
                }

                varName = input[i].trim().split(" ")[2];
                varLine = Integer.parseInt(input[i].trim().split(" ")[1]);

                // Checking that ZA doesn't use the same variable multiple times
                if(za) {
                    for(int j = i + 1; j < input.length; j++) {
                        if(input[j].trim().split(" ")[0].compareTo("<lista_naredbi>") == 0)
                            break;
                        if(input[j].trim().split(" ")[0].compareTo("IDN") == 0) {
                            if(varName.compareTo(input[j].trim().split(" ")[2]) == 0) {
                                output.append("err " + varLine + " " + varName);
                                return output.toString();
                            }
                            else {
                                List<Integer> possible = new ArrayList<>();
                                List<Integer> currentList = varMap.get(input[j].trim().split(" ")[2]);
                                int[] tmp = {-1, -1};

                                for(int k = 0; k < currentList.size() - 1; k += 2) {
                                    // Cheking that the used variable is defined in the current scope
                                    if(currentList.get(++k) > tmp[1] && currentList.get(k) != -1) {
                                        if(varLine > currentList.get(--k)) {
                                            tmp[0] = currentList.get(k);
                                            tmp[1] = currentList.get(++k);
                                            --k;
                                        }
                                        else if(tmp[0] > currentList.get(k)){
                                            tmp[0] = currentList.get(k);
                                            tmp[1] = currentList.get(++k);
                                            --k;
                                        }
                                    }
                                    else --k;

                                }

                                firstLine = tmp[0];

                                if(firstLine == -1 || firstLine == varLine) {
                                    output.append("err " + varLine + " " + input[j].trim().split(" ")[2]);
                                    return output.toString();
                                }

                                output.append(varLine + " " +
                                        firstLine + " " +
                                        input[j].trim().split(" ")[2] + '\n');
                            }
                        }
                        i = j;
                    }
                }

                if(!varMap.containsKey(varName))
                    varMap.put(varName, new LinkedList<Integer>(Arrays.asList(varLine, zaCounter)));
                else if(varMap.get(varName).isEmpty())
                    varMap.put(varName, new LinkedList<Integer>(Arrays.asList(varLine, zaCounter)));
                else if(za){
                    List<Integer> tmp = new ArrayList<>(varMap.get(varName));
                    tmp.add(varLine);
                    tmp.add(zaCounter);
                    varMap.put(varName, tmp);
                }
            }
            else if(input[i].trim().split(" ")[0].compareTo("IDN") == 0) {
                varName = input[i].trim().split(" ")[2];
                varLine = Integer.parseInt(input[i].trim().split(" ")[1]);

                if(!varMap.containsKey(varName)) {
                    output.append("err " + varLine + " " + varName);
                    return output.toString();
                }
                else {
                    List<Integer> possible = new ArrayList<>();
                    List<Integer> currentList = varMap.get(varName);
                    int[] tmp = {-1, -1};

                    for(int j = 0; j < currentList.size() - 1; j += 2) {
                        // Cheking that the used variable is defined in the current scope
                        if(currentList.get(++j) > tmp[1] && currentList.get(j) != -1) {
                            if(varLine > currentList.get(--j)) {
                                tmp[0] = currentList.get(j);
                                tmp[1] = currentList.get(++j);
                                --j;
                            }
                            else if(tmp[0] > currentList.get(j)){
                                tmp[0] = currentList.get(j);
                                tmp[1] = currentList.get(++j);
                                --j;
                            }
                        }
                        else --j;

                    }

                    firstLine = tmp[0];

                    if(firstLine == -1 || firstLine == varLine) {
                        output.append("err " + varLine + " " + varName);
                        return output.toString();
                    }

                    output.append(varLine + " " +
                            firstLine + " " +
                            varName + '\n');
                }
            }
        }

        return output.toString();
//        return "";
    }
}

public class SemantickiAnalizator {
    public static void main(String[] args) {
//        System.out.println("First Line, Number of Spaces");
//        tester();
//        testOneFile("integration/test29/test.in");
        userInput();
    }

    static void tester() {
        String path = "integration/test";
        String folder = "";

        List<Integer> correctList = new ArrayList<>();
        List<Integer> wrongList = new ArrayList<>();


        for(Integer i = 0; i <= 55; i++) {
            try {
                path = "integration/test";
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

                SemAnalyzer analyze = new SemAnalyzer(input.toString());
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
                    correctList.add(i);
                }
                else {
                    System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                    System.out.println("Correct: " + correct);
                    System.out.println("MyOutput:" + myOutput);
                    wrongList.add(i);
                }
            } catch (Exception e) {
                System.out.print(i + "\t");
                System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                System.out.println(e);
                wrongList.add(i);
            }
        }
        System.out.println("\u001B[32m" + "Correct: " + correctList.size() + "\u001B[0m");
        System.out.println(correctList);
        System.out.println("\u001B[31m" + "Wrong: " + wrongList.size() + "\u001B[0m");
        System.out.println(wrongList);
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

        SemAnalyzer analyze = new SemAnalyzer(input.toString());
        System.out.println(analyze.start());
//        System.out.println(analyze.output.toString());

//        for (String s : analyze.input) {
//            System.out.println(s);
//        }
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

        SemAnalyzer machine = new SemAnalyzer(input);

        System.out.println(machine.start());

//        System.out.println(machine.output.toString());
    }
}