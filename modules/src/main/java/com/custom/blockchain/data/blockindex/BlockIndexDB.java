package com.custom.blockchain.data.blockindex;

import org.iq80.leveldb.DBIterator;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.AbstractLevelDB;
import com.custom.blockchain.data.blockindex.dto.BlockIndexDTO;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockIndexDB extends AbstractLevelDB<Long, BlockIndexDTO> {

	@Override
	public BlockIndexDTO get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(String key, BlockIndexDTO value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public BlockIndexDTO get(Long key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Long key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(Long key, BlockIndexDTO value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Long key) {
		// TODO Auto-generated method stub

	}

	@Override
	public DBIterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockIndexDTO next(DBIterator iterator) {
		// TODO Auto-generated method stub
		return null;
	}

}
