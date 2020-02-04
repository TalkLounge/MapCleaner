/*
Author: TalkLounge
Mail: talklounge@yahoo.de

***Build***
"C:\Program Files\Java\jdk1.8.0_151\bin\jar.exe" xf sqlite-jdbc-3.23.1.jar
"C:\Program Files\Java\jdk1.8.0_151\bin\javac.exe" -cp . MapCleaner.java
"C:\Program Files\Java\jdk1.8.0_151\bin\jar.exe" cfe MapCleaner.jar MapCleaner .

***Execute***
java -jar MapCleaner.jar
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

public class MapCleaner {
	static long pythonmodulo(long i, int mod) {
		if (i >= 0) {
			return i % mod;
		}
		return mod - ((-1 * i) % mod);
	}
	static long unsignedToSigned(long i, int max_positive) {
		if (i < max_positive) {
			return i;
		} else {
			return i - (2 * max_positive);
		}
	}
	static int[] getIntegerAsBlock(long i) {
		int[] Pos = new int[3];
		Pos[0] = (int) unsignedToSigned(pythonmodulo(i, 4096), 2048);
		i = (i - Pos[0]) / 4096;
		Pos[1] = (int) unsignedToSigned(pythonmodulo(i, 4096), 2048);
		i = (i - Pos[1]) / 4096;
		Pos[2] = (int) unsignedToSigned(pythonmodulo(i, 4096), 2048);
		Pos[0] = Pos[0] * 16;
		Pos[1] = Pos[1] * 16;
		Pos[2] = Pos[2] * 16;
		return Pos;
	}
	public static void main(String args[]) throws IOException {
		System.out.print("*** MapCleaner ***\n\n");
		System.out.print("Do you want to vacuum the database? Type y/n\n");
		Scanner sc = new Scanner(System.in);
		String In = sc.next().toLowerCase();
		sc.close();
		System.out.print("\n1/4 | Read areas.txt: 0%");
		long FileLength = 0;
		BufferedReader Reader = new BufferedReader(new FileReader("areas.txt"));
		while (Reader.readLine() != null) {
			FileLength++;
		}
		Reader.close();
		int[][][] Areas = new int[(int) FileLength][2][3];
		FileInputStream FileStream = new FileInputStream("areas.txt");
		Reader = new BufferedReader(new InputStreamReader(FileStream));
		long CurrLine = 0;
		String Line;
		while ((Line = Reader.readLine()) != null) {
			String[] Poses = Line.split(":");
			String[] Pos1 = Poses[0].split(",");
			String[] Pos2 = Poses[1].split(",");
			Areas[(int) CurrLine][0][0] = Integer.parseInt(Pos1[0]) < Integer.parseInt(Pos2[0]) ? Integer.parseInt(Pos1[0]) - 96 : Integer.parseInt(Pos2[0]) - 96;
			Areas[(int) CurrLine][0][1] = Integer.parseInt(Pos1[1]) < Integer.parseInt(Pos2[1]) ? Integer.parseInt(Pos1[1]) - 96 : Integer.parseInt(Pos2[1]) - 96;
			Areas[(int) CurrLine][0][2] = Integer.parseInt(Pos1[2]) < Integer.parseInt(Pos2[2]) ? Integer.parseInt(Pos1[2]) - 96 : Integer.parseInt(Pos2[2]) - 96;
			Areas[(int) CurrLine][1][0] = Integer.parseInt(Pos2[0]) > Integer.parseInt(Pos1[0]) ? Integer.parseInt(Pos2[0]) + 96 : Integer.parseInt(Pos1[0]) + 96;
			Areas[(int) CurrLine][1][1] = Integer.parseInt(Pos2[1]) > Integer.parseInt(Pos1[1]) ? Integer.parseInt(Pos2[1]) + 96 : Integer.parseInt(Pos1[1]) + 96;
			Areas[(int) CurrLine][1][2] = Integer.parseInt(Pos2[2]) > Integer.parseInt(Pos1[2]) ? Integer.parseInt(Pos2[2]) + 96 : Integer.parseInt(Pos1[2]) + 96;
			CurrLine++;
		}
		Reader.close();
		System.out.print("\r1/4 | Read areas.txt: 100%\n");
		System.out.print("2/4 | Read map.sqlite: 0%");
		Connection Connection;
		Statement Statement;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection = DriverManager.getConnection("jdbc:sqlite:map.sqlite");
			Connection.setAutoCommit(false);
			Statement = Connection.createStatement();
			ResultSet Result = Statement.executeQuery("SELECT COUNT(*) FROM blocks");
			while (Result.next()) {
				FileLength = Result.getInt(1);
			}
			Result = Statement.executeQuery("SELECT * FROM blocks;");
			FileWriter File = new FileWriter("temp.txt", true);
			long DeleteCount = 0;
			int percent = 0;
			while (Result.next()) {
				long PosInt = Result.getLong("pos");
	            int[] Pos = getIntegerAsBlock(PosInt);
	            boolean inside = false;
	            for (int i = 0; i < Areas.length; i++) {
	            	if ((Pos[0] >= Areas[i][0][0] && Pos[0] <= Areas[i][1][0]) && (Pos[1] >= Areas[i][0][1] && Pos[1] <= Areas[i][1][1]) && (Pos[2] >= Areas[i][0][2] && Pos[2] <= Areas[i][1][2])) {
	            		inside = true;
	            		break;
	            	}
	            }
	            if (! inside) {
	            	DeleteCount++;
	            	File.write(PosInt + "\n");
	            }
	            if (Math.floor((Result.getRow() / (double) FileLength) * 100) != percent) {
	            	percent = (int) Math.floor((Result.getRow() / (double) FileLength) * 100);
	            	System.out.print("\r2/4 | Read map.sqlite: " + percent + "%");
	            }
			}
			File.close();
			System.out.print("\r2/4 | Read map.sqlite: 100%\n");
			System.out.print("3/4 | Save map.sqlite: 0%");
			FileStream = new FileInputStream("temp.txt");
			Reader = new BufferedReader(new InputStreamReader(FileStream));
			CurrLine = 0;
			percent = 0;
			while ((Line = Reader.readLine()) != null) {
				Statement.executeUpdate("DELETE FROM blocks WHERE pos = " + Line);
				if (Math.floor((CurrLine / (double) DeleteCount) * 100) != percent) {
					percent = (int) Math.floor((CurrLine / (double) DeleteCount) * 100);
					System.out.print("\r3/4 | Save map.sqlite: " + percent + "%");
				}
				CurrLine++;
	        }
			Connection.commit();
			Reader.close();
			Statement.close();
			Connection.close();
			File Temp = new File("temp.txt");
			Temp.delete();
			System.out.print("\r3/4 | Save map.sqlite: 100%\n\n");
			System.out.print("Deleted " + ((int) Math.floor((DeleteCount / (double) FileLength) * 100)) + "% (" + DeleteCount + "/" + FileLength + " entries)\n\n");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
	    }
		if (! (In.equals("y") || In.equals("yes"))) {
			System.out.print("Finish!");
			System.exit(0);
		}
		System.out.print("4/4 | Vacuum map.sqlite: 0%");
		try {
			Connection = DriverManager.getConnection("jdbc:sqlite:map.sqlite");
			Statement = Connection.createStatement();
			Statement.executeUpdate("VACUUM");
			Statement.close();
			Connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.print("\r4/4 | Vacuum map.sqlite: 100%\n\n");
		System.out.print("Finish!\n");
	}
}