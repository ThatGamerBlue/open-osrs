package net.runelite.deob.deobfuscators.rsmaths.expressions;

import org.objectweb.asm.tree.analysis.BasicValue;

public class IntegerLdcExpr extends Expr
{
	private final Number number;

	public IntegerLdcExpr(BasicValue basicValue, Number number)
	{
		super(basicValue);
		this.number = number;
	}

	public Number getNumber()
	{
		return number;
	}
}
