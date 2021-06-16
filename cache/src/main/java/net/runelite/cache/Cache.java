/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.runelite.cache.definitions.ScriptDefinition;
import net.runelite.cache.definitions.loaders.ScriptLoader;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import net.runelite.cache.script.disassembler.Disassembler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cache
{
	public static void main(String[] args) throws IOException
	{
		Options options = new Options();

		options.addOption("c", "cache", true, "cache base");

		options.addOption(null, "items", true, "directory to dump items to");
		options.addOption(null, "npcs", true, "directory to dump npcs to");
		options.addOption(null, "objects", true, "directory to dump objects to");
		options.addOption(null, "sprites", true, "directory to dump sprites to");
		options.addOption(null, "scripts", true, "directory to dump scripts to");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try
		{
			cmd = parser.parse(options, args);
		}
		catch (ParseException ex)
		{
			System.err.println("Error parsing command line options: " + ex.getMessage());
			System.exit(-1);
			return;
		}

		String cache = cmd.getOptionValue("cache");

		Store store = loadStore(cache);

		if (cmd.hasOption("items"))
		{
			String itemdir = cmd.getOptionValue("items");

			if (itemdir == null)
			{
				System.err.println("Item directory must be specified");
				return;
			}

			System.out.println("Dumping items to " + itemdir);
			dumpItems(store, new File(itemdir));
		}
		else if (cmd.hasOption("npcs"))
		{
			String npcdir = cmd.getOptionValue("npcs");

			if (npcdir == null)
			{
				System.err.println("NPC directory must be specified");
				return;
			}

			System.out.println("Dumping npcs to " + npcdir);
			dumpNpcs(store, new File(npcdir));
		}
		else if (cmd.hasOption("objects"))
		{
			String objectdir = cmd.getOptionValue("objects");

			if (objectdir == null)
			{
				System.err.println("Object directory must be specified");
				return;
			}

			System.out.println("Dumping objects to " + objectdir);
			dumpObjects(store, new File(objectdir));
		}
		else if (cmd.hasOption("sprites"))
		{
			String spritedir = cmd.getOptionValue("sprites");

			if (spritedir == null)
			{
				System.err.println("Sprite directory must be specified");
				return;
			}

			System.out.println("Dumping sprites to " + spritedir);
			dumpSprites(store, new File(spritedir));
		}
		else if (cmd.hasOption("scripts"))
		{
			String scriptdir = cmd.getOptionValue("scripts");

			if (scriptdir == null)
			{
				System.err.println("Script directory must be specified");
				return;
			}

			System.out.println("Dumping scripts to " + scriptdir);
			dumpScripts(store, new File(scriptdir));
		}
		else
		{
			System.err.println("Nothing to do");
		}
	}

	private static Store loadStore(String cache) throws IOException
	{
		Store store = new Store(new File(cache));
		store.load();
		return store;
	}

	private static void dumpItems(Store store, File itemdir) throws IOException
	{
		ItemManager dumper = new ItemManager(store);
		dumper.load();
		dumper.export(itemdir);
		dumper.java(itemdir);
	}

	private static void dumpNpcs(Store store, File npcdir) throws IOException
	{
		NpcManager dumper = new NpcManager(store);
		dumper.load();
		dumper.dump(npcdir);
		dumper.java(npcdir);
	}

	private static void dumpObjects(Store store, File objectdir) throws IOException
	{
		ObjectManager dumper = new ObjectManager(store);
		dumper.load();
		dumper.dump(objectdir);
		dumper.java(objectdir);
	}

	private static void dumpSprites(Store store, File spritedir) throws IOException
	{
		SpriteManager dumper = new SpriteManager(store);
		dumper.load();
		dumper.export(spritedir);
	}

	private static void dumpScripts(Store store, File scriptdir) throws IOException
	{
		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.CLIENTSCRIPT);
		ScriptLoader loader = new ScriptLoader();

		for (Archive archive : index.getArchives())
		{
			byte[] contents = archive.decompress(storage.loadArchive(archive));

			if (contents == null)
			{
				continue;
			}

			ScriptDefinition script = loader.load(archive.getArchiveId(), contents);

			File outFile = new File(scriptdir, archive.getArchiveId() + ".rs2asm");
			File hashFile = new File(scriptdir, archive.getArchiveId() + ".hash");

			Disassembler disassembler = new Disassembler();
			String out = disassembler.disassemble(script);

			Files.write(out.getBytes(StandardCharsets.UTF_8), outFile);
			String hash = Hashing.sha256().hashBytes(contents).toString().toUpperCase();
			Files.write(hash.getBytes(StandardCharsets.UTF_8), hashFile);
		}
	}
}
