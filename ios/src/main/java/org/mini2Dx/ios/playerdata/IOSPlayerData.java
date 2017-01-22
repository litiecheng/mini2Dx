/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ios.playerdata;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.files.PlayerData;
import org.mini2Dx.core.files.FileStorageException;
import org.mini2Dx.core.serialization.SerializationException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * iOS implementation of {@link PlayerData}
 */
public class IOSPlayerData implements PlayerData {

	@Override
	public <T> T readXml(Class<T> clazz, String... filepath)
			throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			return Mdx.xml.fromXml(new InputStreamReader(resolve(filepath).read()), clazz);
		} catch (SerializationException e) {
			throw new FileStorageException(e);
		}
	}

	@Override
	public <T> void writeXml(T object, String... filepath)
			throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			ensureDirectoryExistsForFile(filepath);
			StringWriter writer = new StringWriter();
			Mdx.xml.toXml(object, writer);
			resolve(filepath).writeString(writer.toString(), false);
			writer.flush();
		} catch (SerializationException e) {
			throw new FileStorageException(e);
		}
	}

	@Override
	public <T> T readJson(Class<T> clazz, String... filepath)
			throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			return Mdx.json.fromJson(resolve(filepath).readString(), clazz);
		} catch (SerializationException e) {
			throw new FileStorageException(e);
		}
	}

	@Override
	public <T> void writeJson(T object, String... filepath)
			throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			resolve(filepath).writeString(Mdx.json.toJson(object), false);
		} catch (SerializationException e) {
			throw new FileStorageException(e);
		}
	}
	
	@Override
	public String readString(String... filepath) throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			FileHandle file = resolve(filepath);
			return file.readString();
		} catch (Exception e) {
			throw new FileStorageException(e);
		}
	}

	@Override
	public void writeString(String content, String... filepath)
			throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		try {
			FileHandle file = resolve(filepath);
			file.writeString(content, false);
		} catch (Exception e) {
			throw new FileStorageException(e);
		}
	}

	@Override
	public boolean delete(String... path) throws FileStorageException {
		if (path.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		FileHandle file = resolve(path);
		if(file.isDirectory()) {
			return file.deleteDirectory();
		}
		return file.delete();
	}

	@Override
	public boolean hasFile(String... filepath) throws FileStorageException {
		if (filepath.length == 0) {
			throw new FileStorageException("No file path specified");
		}
		FileHandle file = resolve(filepath);
		if (file.exists()) {
			return !file.isDirectory();
		}
		return false;
	}

	@Override
	public boolean hasDirectory(String... path) throws FileStorageException {
		if (path.length == 0) {
			throw new FileStorageException("No path specified");
		}
		FileHandle directoryHandle = resolve(path);
		if (directoryHandle.exists()) {
			return directoryHandle.isDirectory();
		}
		return false;
	}

	@Override
	public void createDirectory(String... path) throws FileStorageException {
		if (path.length == 0) {
			throw new FileStorageException("No path specified");
		}
		FileHandle directory = resolve(path);
		if (directory.exists()) {
			return;
		}
		directory.mkdirs();
	}

	@Override
	public void wipe() throws FileStorageException {
		FileHandle directory = Gdx.files.local("./");
		if (!directory.exists()) {
			return;
		}
		directory.emptyDirectory();
	}

	private void ensureDirectoryExistsForFile(String... filepath) {
		FileHandle file = resolve(filepath);
		if (file.exists()) {
			return;
		}
		FileHandle parent = file.parent();
		if (parent.exists()) {
			return;
		}
		parent.mkdirs();
	}

	private FileHandle resolve(String[] filepath) {
		String path = "";
		for (int i = 0; i < filepath.length; i++) {
			path += filepath[i];
			if (path.length() < filepath.length - 1) {
				path += "/";
			}
		}
		return Gdx.files.local(path);
	}
}
