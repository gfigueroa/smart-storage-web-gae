/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package util;

import java.util.Random;

/**
 * Utility class for generating random numbers and strings
 * 
 */
public class RandomGenerator {
    
    /**
     * Generates a random String of the given min and max size.
     */
    public static String generateRandomString(int minSize, int maxSize) {
    	
    	if (minSize <1) {
    		minSize = 1;
    	}
    	if (maxSize < minSize) {
    		maxSize = minSize;
    	}
    	
        // Letters
        char[] chars = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z', '_',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
                'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        
        String varChar = "";
        
        int randomSize = (int) (Math.random() * maxSize) + minSize;
        
        for (int i = 0; i < randomSize; i++) {
            int randomCharNumber = (int) (Math.random() * chars.length);
            char randomChar = chars[randomCharNumber];
            varChar += randomChar;
        }
        
        return varChar;
    }
	
    public static int generateRandomInteger(int min, int max) {
        int rand = (int) (Math.random() * (max - min + 1)) + min;
        return rand;
    }
    
    public static double generateRandomDouble(double min, double max) {
        double rand = (double) (Math.random() * (max - min)) + min;
        return rand;
    }

    public static int generateRandomGaussian(int min, int max) {
        Random r = new Random();

        double mean = (double) (max + min) / 2.0;
        double std = (double) (max - min) / 5.0;

        double rand = mean + std * r.nextGaussian();
        return (int) rand;
    }

    public static int generateRandomExponential(int min, int max) {
        Random r = new Random();

        double mean = (double) max;
        double std = (double) (max - min) / 5.0;

        double rand = mean + std * r.nextGaussian();

        if (rand > mean) {
            double deviation = rand - mean;
            rand = mean - deviation;
        }

        return (int) rand;
    }
    
    /**
     * Generates a random integer value with a particular distribution.
     * The kinds of distribution are: 'UNIFORM', 'NORMAL', 'EXPONENTIAL'
     */
    public static Integer generateDistributedRandomInteger(String distribution,
            int min, int max) {
        if (distribution.equalsIgnoreCase("UNIFORM")) {
            return generateRandomInteger(min, max);
        }
        else if(distribution.equalsIgnoreCase("NORMAL")) {
            return generateRandomGaussian(min, max);
        }
        else if (distribution.equalsIgnoreCase("EXPONENTIAL")) {
            return generateRandomExponential(min, max);
        }
        else {
            return null;
        }
    }

}
