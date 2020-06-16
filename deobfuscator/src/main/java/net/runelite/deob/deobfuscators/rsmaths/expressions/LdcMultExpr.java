package net.runelite.deob.deobfuscators.rsmaths.expressions;

import org.objectweb.asm.tree.analysis.BasicValue;

public class LdcMultExpr extends Expr
{
	private final Number n;

	public LdcMultExpr(BasicValue basicValue, Number n)
	{
		super(basicValue);
		this.n = n;
	}

	public Number getNumber()
	{
		return n;
	}
}
