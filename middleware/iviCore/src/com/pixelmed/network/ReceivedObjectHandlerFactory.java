package com.pixelmed.network;

import java.io.File;

public interface ReceivedObjectHandlerFactory {

	public abstract ReceivedObjectHandler newInstance(File imageFolder);

}