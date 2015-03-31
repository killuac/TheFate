package com.test.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.test.other.Base;
import com.test.other.Shit;

public class Test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("com.test.other.Shit");
			Constructor<?> constructor = (Constructor<?>) cls.getConstructor(Integer.class);
			Base base = (Base) constructor.newInstance(1);
			System.out.println(Base.class.getPackage().getName());
			base.print();
			
			Deque<Integer> test = new ArrayDeque<Integer>(Arrays.asList(1, 2, 3, 4, 5));
			System.out.println(test);
			
			Deque<Integer> descend = new ArrayDeque<Integer>();
			Iterator<Integer> itr = test.descendingIterator();
			int index = 0;
			while (itr.hasNext()) {
				if (index >= 2) break;
				descend.add(itr.next());
				index++;
			}
			System.out.println(descend);
			
			AbstractMap<Integer, Shit> maps = new ConcurrentHashMap<Integer, Shit>();
			maps.put(1, new Shit(100));
			maps.put(2, new Shit(200));
//			Collection<Shit> list = (Collection<Shit>) maps.values();
			System.out.println(maps.values().getClass().getName());
			
//			System.out.println(Base.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			Date dateTime = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateTime);
			calendar.add(Calendar.DATE, 1);
			System.out.println(DateFormat.getDateInstance().format(dateTime));
			System.out.println(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA).format(dateTime));
			System.out.println(DateFormat.getDateTimeInstance().format(calendar.getTime()));
			
			
			double d = 54.12535;
			System.out.println(new DecimalFormat("#.#").format(d));
			System.out.println((int) (Math.random()*1000000));
			
//			System.out.println(System.getProperty("java.class.path"));
			System.out.println(System.getProperty("user.dir"));
			
			System.out.println(new Test().generateAIName());
			
			System.out.println("Default system encoding: " + System.getProperty("file.encoding"));
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String generateAIName() {
		String charSet = "ABCDEFGHIGKLMNOPQRSTUVWXYZ";
		Random random = new Random(System.currentTimeMillis());
		String alpha = String.valueOf(charSet.charAt(random.nextInt(charSet.length())));
		int numeric = (int) (random.nextDouble() * 1000000000);
		return alpha + numeric;
	}
	
}
