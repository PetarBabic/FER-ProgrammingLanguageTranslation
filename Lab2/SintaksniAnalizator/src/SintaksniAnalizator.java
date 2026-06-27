/*
<program> ::= <lista_naredbi> = (IDN KR_ZA KRAJ}

<lista_naredbi> ::= <naredba> <lista_naredbi> = {IDN KR_ZA}
<lista_naredbi> ::= $ = (KR_AZ KRAJ}

<naredba> ::= <naredba_pridruzivanja> = (IDN}
<naredba> ::= <za_petlja> = (KR_ZA}

<naredba_pridruzivanja> ::= IDN OP_PRIDRUZI <E> = {IDN}

<za_petIja> ::= KR_ZA IDN KR_OD <E> KR_DO <E> <lista_naredbi> KR_AZ = (KR_ZA}

<E> ::= <T> <E_lista> = {IDN BROJ OP_PLUS OP_MINUS L_ZAGRADA}

<E_lista> ::= OP_PLUS <E> = {OP_PLUS}
<E_lista> ::= OP_MINUS <E> = {OP_MINUS}
<E_lista> ::= $ = {IDN KR_ZA KR_DO KR_AZ D_ZAGRADA KRAJ}

<T> ::= <P> <T_lista> = {IDN BROJ OP_PLUS OP_MINUS L_ZAGRADA}

<T_lista> ::= OP_PUTA <T> = {OP_PUTA}
<T_lista> ::= OP_DIJELI <T> = {OP_DIJELI}
<T_lista> ::= $ = (IDN KR_ZA KR_DO KR_AZ OP_PLUS OP_MINUS D_ZAGRADA KRAJ}

<P> ::= OP_PLUS <P> = {OP_PLUS}
<P> ::= OP_MINUS <P> = {OP_MINUS}
<P> ::= L_ZAGRADA <E> D_ZAGRADA = {L_ZAGRADA}
<P> ::= IDN = {IDN}
<P> ::= BROJ = {BROJ}

ERROR: err <uniformni_znak> <br_retka> <leksicka_jedinka>
*/

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

class Syntax{
    String input[];
    StringBuilder output = new StringBuilder();

    public Syntax(String input) {
        this.input = input.split("\n");
    }

    private void buildTree(String txt, int numOfSpaces) {
        StringBuilder spaces = new StringBuilder();

        for(int i = 0; i < numOfSpaces; i++)
            spaces.append(' ');

        output.append(spaces + txt + '\n');
    }

    public void start() {
        try {
            program(0, 0);
        } catch (Exception e) {
            output = new StringBuilder(e.getMessage() + '\n');
        }
    }

    private void program(int line, int spaces) throws Exception {
        String statment = getStatment(input[line]);
        int tmp;

        buildTree("<program>", spaces);

        if(statment.compareTo("IDN") == 0 || statment.compareTo("KR_ZA") == 0)
            tmp = lista_naredbi(line, spaces);
        else
            throw new Exception("err " + input[line]);
    }

    private int lista_naredbi(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<lista_naredbi>", spaces);

        if(line >= input.length) {
            buildTree("$", ++spaces);
            return line;
        }
        String statment = getStatment(input[line]);

        if(statment.compareTo("IDN") == 0 || statment.compareTo("KR_ZA") == 0) {
            line = naredba(line, spaces);
            line = lista_naredbi(line, spaces);
        }
        else if(statment.compareTo("KR_AZ") == 0) {
            buildTree("$", ++spaces);
            return line;
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int naredba(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<naredba>", spaces);
        String statment = getStatment(input[line]);

        if(statment.compareTo("IDN") == 0)
            line = naredba_pridruzivanja(line, spaces);
        else if(statment.compareTo("KR_ZA") == 0)
            line = za_petIja(line, spaces);
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int naredba_pridruzivanja(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<naredba_pridruzivanja>", spaces);
        String statment = getStatment(input[line]);

        spaces++;

        if(statment.compareTo("IDN") == 0) {
            buildTree(input[line++], spaces); // IDN

            if(getStatment(input[line]).compareTo("OP_PRIDRUZI") == 0)
                buildTree(input[line++], spaces); // OP_PRIDRUZI
            else throw new Exception("err " + input[line]);

            line = E(line, --spaces);
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int za_petIja(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<za_petlja>", spaces);
        String statment = getStatment(input[line]);

        if(statment.compareTo("KR_ZA") == 0) {
            buildTree(input[line++], ++spaces); //KR_ZA

            if(getStatment(input[line]).compareTo("IDN") == 0)
                buildTree(input[line++], spaces); //IDN
            else throw new Exception("err " + input[line]);

            if(getStatment(input[line]).compareTo("KR_OD") == 0)
                buildTree(input[line++], spaces); //KR_OD
            else throw new Exception("err " + input[line]);

            line = E(line, --spaces);

            if(getStatment(input[line]).compareTo("KR_DO") == 0)
                buildTree(input[line++], ++spaces); //KR_DO
            else throw new Exception("err " + input[line]);

            line = E(line, --spaces);
            line = lista_naredbi(line, spaces);

            if(getStatment(input[line]).compareTo("KR_AZ") == 0)
                buildTree(input[line++], ++spaces); //KR_AZ
            else throw new Exception("err " + input[line]);
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int E(int line, int spaces) throws Exception {
        spaces++;

        if(line >= input.length) {
            throw new Exception("err kraj");
        }

        buildTree("<E>", spaces);
        String statment = getStatment(input[line]);

        if(statment.compareTo("IDN") == 0
                || statment.compareTo("BROJ") == 0
                || statment.compareTo("OP_PLUS") == 0
                || statment.compareTo("OP_MINUS") == 0
                || statment.compareTo("L_ZAGRADA") == 0) {
            line = T(line, spaces);
            line = E_lista(line, spaces);
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int E_lista(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<E_lista>", spaces);
        if(line >= input.length) {
            buildTree("$", ++spaces);
            return line;
        }
        String statment = getStatment(input[line]);

        if(statment.compareTo("OP_PLUS") == 0 || statment.compareTo("OP_MINUS") == 0){
            buildTree(input[line++], ++spaces);
            line = E(line, --spaces);
        }

        else if(statment.compareTo("IDN") == 0
                || statment.compareTo("KR_ZA") == 0
                || statment.compareTo("KR_DO") == 0
                || statment.compareTo("KR_AZ") == 0
                || statment.compareTo("D_ZAGRADA") == 0) {
            buildTree("$", ++spaces);
            return line;
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int T(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<T>", spaces);
        String statment = getStatment(input[line]);

        if(statment.compareTo("IDN") == 0
                || statment.compareTo("BROJ") == 0
                || statment.compareTo("OP_PLUS") == 0
                || statment.compareTo("OP_MINUS") == 0
                || statment.compareTo("L_ZAGRADA") == 0) {
            line = P(line, spaces);
            line = T_lista(line, spaces);
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private int T_lista(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<T_lista>", spaces);
        if(line >= input.length) {
            buildTree("$", ++spaces);
            return line;
        }
        String statment = getStatment(input[line]);

        if(statment.compareTo("OP_PUTA") == 0 || statment.compareTo("OP_DIJELI") == 0){
            buildTree(input[line++], ++spaces);
            line = T(line, --spaces);
        }
        else if(statment.compareTo("IDN") == 0
                || statment.compareTo("KR_ZA") == 0
                || statment.compareTo("KR_DO") == 0
                || statment.compareTo("KR_AZ") == 0
                || statment.compareTo("OP_PLUS") == 0
                || statment.compareTo("OP_MINUS") == 0
                || statment.compareTo("D_ZAGRADA") == 0) {
            buildTree("$", ++spaces);
            return line;
        }
        else
            throw new Exception("err " + input[line]);


        return line;
    }

    private int P(int line, int spaces) throws Exception {
        spaces++;

        buildTree("<P>", spaces);
        String statment = getStatment(input[line]);

        if(statment.compareTo("OP_PLUS") == 0 || statment.compareTo("OP_MINUS") == 0){
            buildTree(input[line++], ++spaces);
            line = P(line, --spaces);
        }
        else if(statment.compareTo("L_ZAGRADA") == 0) {
            buildTree(input[line++], ++spaces); //L_ZAGRADA
            line = E(line, --spaces);

            if(getStatment(input[line]).compareTo("D_ZAGRADA") == 0)
                buildTree(input[line++], ++spaces); //D_ZAGRADA
            else
                throw new Exception("err " + input[line]);
        }
        else if(statment.compareTo("IDN") == 0 || statment.compareTo("BROJ") == 0){
            buildTree(input[line++], ++spaces);
        }
        else
            throw new Exception("err " + input[line]);

        return line;
    }

    private String getStatment(String text) {
        return text.split(" ")[0];
    }
}

public class SintaksniAnalizator {
    public static void main(String[] args) {
//        testOneFile("integration/test_95/test.in");
//        tester();
        userInput();
    }

    static void tester() {
        String path = "integration/test";
        String folder = "";

        int[] counter = {0, 0};

        for(Integer i = 0; i <= 110; i++) {
            try {
                path = "integration/test_";
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

                Syntax analyze = new Syntax(input.toString());
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

                String myOutput = analyze.output.toString();
                String correct = input.toString();

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

        Syntax analyze = new Syntax(input.toString());
        analyze.start();
        System.out.println(analyze.output.toString());

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

        Syntax machine = new Syntax(input);

        machine.start();

        System.out.println(machine.output.toString());
    }
}