package net.runelite.deob.deobfuscators.rsmaths.expressions;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;

public class GetFieldExpr extends Expr
{
	private final FieldInsnNode insn;

	public GetFieldExpr(BasicValue basicValue, FieldInsnNode insn)
	{
		super(basicValue);
		this.insn = insn;
	}

	public FieldInsnNode getInsn()
	{
		return insn;
	}
}
