package com.lapis_semi.lazurite.LazuriteGraph;

import java.util.*;

public class RawEventObject extends EventObject {
	public final static int RAW_DATA_AVAILABLE = 0x1;
	public final static int DATA_AVAILABLE = 0x1;
	private static final long serialVersionUID = 1L;
	private int event;
	public RawEventObject(int source) {
		super(source);
		event = source;
	}
	public int getEventType(){
		return event;
	}
}
