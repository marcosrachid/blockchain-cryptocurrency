package com.custom.blockchain.data.blockindex;

import org.iq80.leveldb.DB;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockIndexDB {
	
	private DB blockIndexDb;
	
	private ObjectMapper objectMapper;
	
	public BlockIndexDB(final @Qualifier("BlockIndexDB") DB blockIndexDb, final ObjectMapper objectMapper) {
		this.blockIndexDb = blockIndexDb;
		this.objectMapper = objectMapper;
	}

}
