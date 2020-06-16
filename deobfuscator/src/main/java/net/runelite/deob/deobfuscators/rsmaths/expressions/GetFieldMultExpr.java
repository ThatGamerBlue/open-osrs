package net.runelite.deob.deobfuscators.rsmaths.expressions;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;

public class GetFieldMultExpr extends Expr
{
	private final FieldInsnNode insn;
	private final Number n;

	public GetFieldMultExpr(BasicValue basicValue, FieldInsnNode insn, Number n)
	{
		super(basicValue);
		this.insn = insn;
		this.n = n;
	}

	public FieldInsnNode getInsn()
	{
		return insn;
	}

	public Number getNumber()
	{
		return n;
	}
}
