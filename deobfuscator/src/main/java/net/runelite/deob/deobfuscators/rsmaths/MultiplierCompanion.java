package net.runelite.deob.deobfuscators.rsmaths;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

public class MultiplierCompanion
{
	public static Map<String, Number> decoders = new TreeMap<String, Number>();
	public static final BigInteger UNSIGNED_INT_MODULUS = BigInteger.ONE.shiftLeft(Integer.SIZE);
	public static final BigInteger UNSIGNED_LONG_MODULUS = BigInteger.ONE.shiftLeft(Long.SIZE);

	public static boolean isMultiplier(Number number)
	{
		return isInvertible(number) && !(invert(number).equals(number));
	}

	public static boolean isInvertible(Number number)
	{
		return (number.intValue() & 1) == 1;
	}

	public static Number invert(Number number)
	{
		if (number instanceof Integer)
		{
			return BigInteger.valueOf(number.longValue()).modInverse(UNSIGNED_INT_MODULUS).intValue();
		}
		else if (number instanceof Long)
		{
			return BigInteger.valueOf(number.longValue()).modInverse(UNSIGNED_LONG_MODULUS).longValue();
		}
		throw new RuntimeException(number + " is not one of int or long types");
	}
}
