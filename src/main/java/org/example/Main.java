package org.example;

import java.sql.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.lang.Math.abs;
import static java.lang.Math.pow;


public class Main {
    protected static Scanner sc = new Scanner(System.in);
    protected static Connection con;
    protected static String table = "task1";
    static final String schema = "task1";
    static final String selectFromTable = "SELECT * FROM " + table;
    static final String tablePart = ("+" + "-".repeat(5) + "+" + "-".repeat(12) + "+" + "-".repeat(12) + "+" + "-".repeat(17) + "+" + "-".repeat(12) + "+" + "-".repeat(22) + "+" + "-".repeat(17) + "+" + "-".repeat(17) + "+" + "-".repeat(32) + "+");

    static String Url = "jdbc:postgresql://localhost:5432/postgres";

    static {
        try {
            con = DriverManager.getConnection(Url, "postgres", "postgres");
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к базе данных: " + e.getMessage());
        }

        try {
            con.setAutoCommit(false);

            Statement st = con.createStatement();
            st.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + schema);
            st.executeUpdate("SET search_path TO " + schema);

            con.commit();
            con.setAutoCommit(true);
            System.out.println("Используется схема - " + schema);
        } catch (SQLException e) {
            System.out.println("Не удалось создать схему для задания: " + e.getMessage());
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        String query = "CREATE TABLE IF NOT EXISTS task1 (id SERIAL, sum INT, sub INT, mul INT, div INT, mod INT, abs_1 INT, abs_2 INT, pow INT)";
        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
            table = "task1";
            System.out.println("Используется таблица по умолчанию - " + table);
        } catch (SQLException e) {
            System.out.println("Не удалось использовать таблицу по умолчанию, " + e.getMessage());
        }
    }

    protected static void menu() {
        int x = 0;
        String s = "";
        Task tasks = new Task();
        ExportToExcel export = new ExportToExcel();
        while (!"0".equals(s)) {
            System.out.println("Меню программы:");
            System.out.println("1. Вывести все таблицы.");
            System.out.println("2. Создать/выбрать таблицу.");
            System.out.println("3. Cложение двух чисел.");
            System.out.println("4. Разность двух чисел");
            System.out.println("5. Умножение двух чисел");
            System.out.println("6. Деление двух чисел");
            System.out.println("7. Деление по модулю двух чисел");
            System.out.println("8. Модуль двух чисел");
            System.out.println("9. Число в степени другого числа");
            System.out.println("10. Записать результаты в таблицу и вывести");
            System.out.println("11. Записать данные в Excel и вывести");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт меню: ");
            s = sc.nextLine();
            try {
                x = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода");
            }
            switch (x) {
                case 1 -> tasks.task1();
                case 2 -> tasks.task2();
                case 3 -> tasks.task3();
                case 4 -> tasks.task4();
                case 5 -> tasks.task5();
                case 6 -> tasks.task6();
                case 7 -> tasks.task7();
                case 8 -> tasks.task8();
                case 9 -> tasks.task9();
                case 10 -> {
                    tasks.insertData();
                    tasks.selectData();
                }
                case 11 -> {
                    System.out.print("Введите название файла: ");
                    String filepath = sc.nextLine();

                    if (!filepath.contains(".xlsx")) {
                        filepath += ".xlsx";
                    }

                    export.exportData(table, filepath);
                    export.printExcelData(filepath);
                }
                case 0 -> System.out.println("Пока!");
                default -> System.out.println("Неправильно выбран пункт меню! Попробуйте еще раз...");
            }
            x = 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("Подключились к БД. ");
        menu();
    }
}

class Task extends Main {
    static int firstNum;
    static int secondNum;

    static Object sum = null;
    static Object sub = null;
    static Object mul = null;
    static Object div = null;
    static Object mod = null;
    static Object abs_1 = null;
    static Object abs_2 = null;
    static Object pow = null;

    public void task1() {
        String query = "SELECT table_name AS Названия_таблиц FROM information_schema.tables WHERE table_schema = '" + schema + "'";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            try {
                int count = 1;
                int nameLength = 15;
                while (rs.next()) {
                    int currentNameLength = rs.getString(1).length();
                    if (currentNameLength > nameLength) {
                        count++;
                        nameLength = currentNameLength;
                    }
                }
                String tablePart = "+" + "-".repeat(5) + "+" + "-".repeat(nameLength + 2) + "+";
                System.out.println("Список таблиц:");
                System.out.println(tablePart);
                System.out.printf("| %-3s | %-15s |\n", "ID", "Названия таблиц");

                int i = 1;
                while (rs.next()) {
                    String tableName = rs.getString("Названия_таблиц");
                    System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(27) + "+");
                    System.out.printf("| %-3d | %-25s |\n", i++, tableName);
                }
                System.out.println("+" + "-".repeat(5) + "+" + "-".repeat(27) + "+");
            } catch (SQLException e) {
                System.out.println("Не удалось вывести результат, " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
        }
    }

    public void task2() {
        System.out.print("Введите название таблицы: ");
        table = sc.next();
        String query = "CREATE TABLE IF NOT EXISTS " + table + " (id SERIAL, sum INT, sub INT, mul INT, div INT, mod INT, abs_1 INT, abs_2 INT, pow INT)";
        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
            System.out.println("Таблица " + table + " успешно создана/выбрана!");
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
            task2();
        }
    }

    public void task3() {
        inputFirstNum();
        inputSecondNum();

        sum = firstNum + secondNum;
        System.out.println("Сумма чисел: " + sum);
    }


    public void task4() {
        inputFirstNum();
        inputSecondNum();

        sub = firstNum - secondNum;
        System.out.println("Разность чисел: " + sub);
    }

    public void task5() {
        inputFirstNum();
        inputSecondNum();

        mul = firstNum * secondNum;
        System.out.println("Произведение чисел: " + mul);
    }

    public void task6() {
        inputFirstNum();
        inputSecondNum();

        div = firstNum / secondNum;
        System.out.println("Частное чисел: " + div);
    }

    public void task7() {
        inputFirstNum();
        inputSecondNum();

        mod = firstNum % secondNum;
        System.out.println("Остаток от деления чисел: " + mod);
    }

    public void task8() {
        inputFirstNum();
        inputSecondNum();

        abs_1 = abs(firstNum);
        System.out.println("Модуль первого числа: " + abs_1);

        abs_2 = abs(secondNum);
        System.out.println("Модуль второго числа: " + abs_2);
    }

    public void task9() {
        inputFirstNum();
        inputSecondNum();

        pow = pow(firstNum, secondNum);
        System.out.println("Число в степени: " + pow);
    }

    public void insertData() {
        String query = "INSERT INTO " + table + " (sum, sub, mul, div, mod, abs_1, abs_2, pow) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setObject(1, sum);
            pst.setObject(2, sub);
            pst.setObject(3, mul);
            pst.setObject(4, div);
            pst.setObject(5, mod);
            pst.setObject(6, abs_1);
            pst.setObject(7, abs_2);
            pst.setObject(8, pow);
            pst.executeUpdate();
            System.out.println("Все выполненные результаты добавлены в таблицу!");
        } catch (
                SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
        }
    }

    public void selectData() {
        System.out.println("Получаю данные...");
        try (PreparedStatement pst = con.prepareStatement(selectFromTable)) {
            try (ResultSet rs = pst.executeQuery()) {
                System.out.println("Полученные данные: ");
                System.out.println(tablePart);
                System.out.printf("| %3s | %-10s | %-10s | %-15s | %-10s | %-20s | %-15s | %-15s | %-30s |\n", "ID", "Сумма", "Разность", "Произведение", "Частное", "Остаток от деления", "Модуль числа 1", "Модуль числа 2", "Число в степени другого числа");
                while (rs.next()) {
                    int ID = rs.getInt(1);
                    int sum = rs.getInt(2);
                    int sub = rs.getInt(3);
                    int mul = rs.getInt(4);
                    int div = rs.getInt(5);
                    int mod = rs.getInt(6);
                    int abs_1 = rs.getInt(7);
                    int abs_2 = rs.getInt(8);
                    int pow = rs.getInt(9);
                    System.out.println(tablePart);
                    System.out.printf("| %3d | %-10d | %-10d | %-15d | %-10d | %-20d | %-15d | %-15d | %-30d |\n", ID, sum, sub, mul, div, mod, abs_1, abs_2, pow);
                }
                System.out.println(tablePart);
            }
        } catch (SQLException e) {
            System.out.println("Не удалось получить данные из таблицы, " + e.getMessage());
        }
    }

    public void inputFirstNum() {
        while (true) {
            try {
                System.out.print("Введите первое число: ");
                String s = sc.next();
                firstNum = Integer.parseInt(s);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода");
            }
        }
        sc.nextLine();
    }

    public void inputSecondNum() {
        while (true) {
            try {
                System.out.print("Введите второе число: ");
                String s = sc.next();
                secondNum = Integer.parseInt(s);
                System.out.println();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода");
                inputSecondNum();
            }
        }
        sc.nextLine();
    }
}

class ExportToExcel extends Main {
    String filepath;

    public void exportData(String table, String filepath) {
        this.filepath = filepath;

        String printAll = "SELECT * FROM " + table;
        try (PreparedStatement pst = con.prepareStatement(printAll); ResultSet rs = pst.executeQuery()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("task 1");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(rs.getMetaData().getColumnName(1));
            row.createCell(1).setCellValue(rs.getMetaData().getColumnName(2));
            row.createCell(2).setCellValue(rs.getMetaData().getColumnName(3));
            row.createCell(3).setCellValue(rs.getMetaData().getColumnName(4));
            row.createCell(4).setCellValue(rs.getMetaData().getColumnName(5));
            row.createCell(5).setCellValue(rs.getMetaData().getColumnName(6));
            row.createCell(6).setCellValue(rs.getMetaData().getColumnName(7));
            row.createCell(7).setCellValue(rs.getMetaData().getColumnName(8));
            row.createCell(8).setCellValue(rs.getMetaData().getColumnName(9));

            int rowIndex = 1;
            while (rs.next()) {
                Row row1 = sheet.createRow(rowIndex++);
                row1.createCell(0).setCellValue(rs.getInt(1));
                row1.createCell(1).setCellValue(rs.getInt(2));
                row1.createCell(2).setCellValue(rs.getInt(3));
                row1.createCell(3).setCellValue(rs.getInt(4));
                row1.createCell(4).setCellValue(rs.getInt(5));
                row1.createCell(5).setCellValue(rs.getInt(6));
                row1.createCell(6).setCellValue(rs.getInt(7));
                row1.createCell(7).setCellValue(rs.getInt(8));
                row1.createCell(8).setCellValue(rs.getInt(9));

            }
            int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream fos = new FileOutputStream(filepath)) {
                wb.write(fos);
            } catch (IOException e) {
                System.out.println("Ошибка при записи Excel-файла: " + e);
            } finally {
                wb.close();
                System.out.println("Данные успешно сохранены в Excel-файл: " + filepath);
            }
        } catch (IOException | SQLException e) {
            System.out.println("Ошибка при экспорте данных: " + e);
        }
    }

    public void printExcelData(String filepath) {
        try (Workbook wb = new XSSFWorkbook(filepath)) {
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("\nДанные из Excel:");
            for (Row row : sheet) {
                Cell id = row.getCell(0);
                Cell sum = row.getCell(1);
                Cell sub = row.getCell(2);
                Cell mul = row.getCell(3);
                Cell div = row.getCell(4);
                Cell mod = row.getCell(5);
                Cell abs_1 = row.getCell(6);
                Cell abs_2 = row.getCell(7);
                Cell pow = row.getCell(8);

                System.out.println(tablePart);
                System.out.printf("| %3s | %-10s | %-10s | %-15s | %-10s | %-20s | %-15s | %-15s | %-30s |\n", id, sum, sub, mul, div, mod, abs_1, abs_2, pow);
            }
            System.out.println(tablePart);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении Excel-файла: " + e.getMessage());
        }
    }
}
