package net.runelite.deob.deobfuscators.rsmaths.expressions;

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;

public abstract class Expr implements Value
{
	protected final BasicValue basicValue;

	protected Expr(BasicValue basicValue)
	{
		this.basicValue = basicValue;
	}

	public int getSize()
	{
		return getBasicValue().getSize();
	}

	public BasicValue getBasicValue()
	{
		return basicValue;
	}
}
