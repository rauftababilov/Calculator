package com.rauf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static final String ARABIC_EXPRESSION = "^([\\d]{1,2})[\\s]*([\\+\\-\\*\\/]){1}[\\s]*([\\d]{1,2})$";
    private static final String ROMAN_EXPRESSION = "([XIV]{1,3})[\\s]*([\\+\\-\\*\\/]{1})[\\s]*([XIV]{1,3})$";
    private static final Pattern ROMAN_PATTERN = Pattern.compile(ROMAN_EXPRESSION);
    private static final Pattern ARABIC_PATTERN = Pattern.compile(ARABIC_EXPRESSION);

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IllegalInputNumberException
    {
        while(true)
        {
            System.out.println("Введите выражение:");
            int num1;
            int num2;
            char operation;
            String result;
            if(scanner.hasNextLine()) // ждём, когда пользователь наберёт строку
            {
                String expression = scanner.nextLine(); // получаем новую строку
                Matcher matcher = ROMAN_PATTERN.matcher(expression);
                if(matcher.find()) // это выражения с римскими цифрами?
                {
                    num1 = getRomanNumeral(matcher, 1);
                    operation = getOperation(matcher);
                    num2 = getRomanNumeral(matcher, 2);
                    int r = (int) calc(num1,num2,operation);
                    result = arabicToRoman(r);
                }
                else
                {
                    matcher = ARABIC_PATTERN.matcher(expression); // это выражения с арабскими цифрами?
                    if(matcher.find())
                    {
                        num1 = getInt(matcher, 1);
                        operation = getOperation(matcher);
                        num2 = getInt(matcher, 2);
                        result = Float.toString(calc(num1,num2,operation));
                    }
                    else
                    {
                        throw new IllegalInputNumberException("Неверный формат ввода! Строка - " + expression);
                    }
                }

                System.out.println("Результат операции: " + result);
            }
        }
    }

    public static String arabicToRoman(int number) throws IllegalInputNumberException
    {
        if((number <= 0) || (number > 4000))
        {
            throw new IllegalInputNumberException(number + " должно находиться в диапазоне (0,4000]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while((number > 0) && (i < romanNumerals.size()))
        {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number)
            {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            }
            else
            {
                i++;
            }
        }

        return sb.toString();
    }

    private static int getRomanNumeral(Matcher a_matcher, int a_number_index) throws IllegalInputNumberException
    {
        int result;
        try
        {
            /*
             * заранее знаем, что первое число в группе 1, второе число в группе 3
             */
            String roman_numeral = a_matcher.group(a_number_index == 1 ? 1 : 3);
            result = romanToArabic(roman_numeral);
        }
        catch (Exception e)
        {
            throw new IllegalInputNumberException("Неверный формат ввода!");
        }
        return result;
    }

    public static int romanToArabic(String input) throws IllegalInputNumberException
    {
        String romanNumeral = input.toUpperCase();
        int result = 0;
        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size()))
        {
            /*
             * посимвольно считываем входную строку и сравниваем каждый символ
             * крайний левый символ с enum набором, когда находим совпадения, прибавлем
             * значение enum-а к результату
             */
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name()))
            {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            }
            else
            {
                i++;
            }
        }

        if (romanNumeral.length() > 0)
        {
            throw new IllegalInputNumberException(input + " невозможно преобразовать в арабские цифры");
        }

        return result;
    }

    public static int getInt(Matcher a_matcher, int a_number_index) throws IllegalInputNumberException
    {
        int num;
        try
        {
            /*
             * заранее знаем, что первое число в группе 1, второе число в группе 3
             */
            String integer_num = a_matcher.group(a_number_index == 1 ? 1 : 3);
            num = Integer.parseInt(integer_num);
            if(num < 0 || num > 10)
            {
                throw new IllegalInputNumberException("Ошибка! Число должно быть > 0 и < 10.");
            }
        }
        catch (Exception e)
        {
            throw new IllegalInputNumberException("Неверный формат ввода!");
        }
        return num;
    }

    public static char getOperation(Matcher a_matcher) throws IllegalInputNumberException
    {
        char operation;

        try
        {
            /*
             * заранее знаем, что символ операции в группе 2
             */
            operation = a_matcher.group(2).charAt(0);
        }
        catch (Exception e)
        {
            throw new IllegalInputNumberException("Вы допустили ошибку при вводе операции!");
        }
        return operation;
    }

    public static float calc(int num1, int num2, char operation) throws IllegalInputNumberException
    {
        float result;
        switch (operation)
        {
            case '+':
                result = num1+num2;
                break;
            case '-':
                result = num1-num2;
                break;
            case '*':
                result = num1*num2;
                break;
            case '/':
                result = new BigDecimal(num1).divide(new BigDecimal(num2), 2, RoundingMode.HALF_DOWN).floatValue();
                break;
            default:
                throw new IllegalInputNumberException("Операция не распознана!");
        }
        return result;
    }
}
