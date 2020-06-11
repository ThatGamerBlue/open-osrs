/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
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
package net.runelite.client.plugins;

import java.awt.AWTPermission;
import java.io.File;
import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import javax.sound.sampled.AudioPermission;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import org.pf4j.JarPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

@Slf4j
public class SecureJarPluginLoader extends JarPluginLoader
{
	private static final Permission[] allowedPermissions = new Permission[]{
		// AWT Permissions: https://docs.oracle.com/javase/7/docs/api/java/awt/AWTPermission.html
		new AWTPermission("accessClipboard"),
		//new AWTPermission("accessEventQueue"),
		new AWTPermission("createRobot"),

		// Audio Permissions: https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioPermission.html
		new AudioPermission("play"),

		// Crypto Permissions?

		// File Permissions: https://docs.oracle.com/javase/7/docs/api/java/io/FilePermission.html
		new FilePermission(RuneLite.RUNELITE_DIR.getAbsolutePath() + File.separator + "-", "read,write,delete"),

		// TODO: finish this list
	};

	public SecureJarPluginLoader(PluginManager pluginManager)
	{
		super(pluginManager);
	}

	@Override
	public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor)
	{
		return AccessController.doPrivileged(new PrivilegedAction<PluginClassLoader>()
		{
			@Override
			public PluginClassLoader run()
			{
				PluginClassLoader pluginClassLoader =
					new PluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
				pluginClassLoader.addFile(pluginPath.toFile());
				return pluginClassLoader;
			}
		}, createAcc(pluginPath));
	}

	private AccessControlContext createAcc(Path pluginPath)
	{
		URL jarUrl;
		try
		{
			jarUrl = pluginPath.toAbsolutePath().toUri().toURL();
		}
		catch (MalformedURLException e)
		{
			log.error("Error creating security context for plugin {}", pluginPath);
			// this will cause pf4j to throw an exception down the line, we catch it in ExternalPf4jPluginManager and deal with it there
			return null;
		}
		CodeSource cs = new CodeSource(jarUrl, (CodeSigner[]) null);
		Permissions p = new Permissions();
		Arrays.stream(allowedPermissions).forEach(p::add);
		ProtectionDomain pd = new ProtectionDomain(cs, p);
		return new AccessControlContext(new ProtectionDomain[]{pd});
	}
}
