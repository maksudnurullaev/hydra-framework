package org.hydra.db.server;

import java.util.EnumMap;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.utils.abstracts.ALogger;

public class CassandraVirtualPath extends ALogger {
	// Error codes
	public enum ERR_CODES {
		UNDEFINED, 
		NO_ERROR, 
		INVALID_DESCRIPTOR, 
		INVALID_PATH, 
		INVALID_KS, 
		INVALID_CF, 
		INVALID_COLUMN, 
		INVALID_LINK, 
		INVALID_PATH_STRUCTURE, 
		INVALID_DIC_TYPE
	};

	ERR_CODES _errCode = ERR_CODES.NO_ERROR;
	
	public ERR_CODES getErrorCode() {
		return _errCode;
	}
	
	String _errString = null;
	
	public String getError(){
		return _errString;
	}
	
	private void setError(String inErrString){
		getLog().error(inErrString);
		_errString = inErrString;
	}

	// Path parts
	public enum PARTS {
		KSP, CF, /* KEY, - unnecessary */SUPE_R, COL
	};

	// Result types
	public enum RESULT_TYPES {
		UNDEFINED, 
		DATA_KSP_CF,		 				/* get IDs */
		DATA_KSP_CF_COLUMNS, 				/* get column descriptions */
		DATA_KSP_CF_COLUMNS_SUPER, 			/* get columns for certain ID (super) */
		DATA_KSP_CF_COLUMNS_SUPER_COLUMN,	/* get certain column from columns for certain ID (super) */
		DATA_KSP_CF_LINKS, 					/* get links descriptions */
		DATA_KSP_CF_LINKS_SUPER_COLUMN,		/* get linked ID's for certain ID (super) */ 
	};

	RESULT_TYPES _resultType = RESULT_TYPES.UNDEFINED;

	// Map contains parsed parts of access path
	public EnumMap<PARTS, String> _pathMap = new EnumMap<PARTS, String>(
			PARTS.class);

	// Path definitions as array
	String[] pathDefs = null;

	// Access path string delimeter
	final String PATH_DELIMETER = "\\.";

	CassandraDescriptorBean cassandraDescriptorBean = null;

	public CassandraVirtualPath(
			CassandraDescriptorBean inCassandraDescriptorBean, String inPath) {
		if (inCassandraDescriptorBean == null) {
			setError("CassandraDescriptor is NULL");
			_errCode = ERR_CODES.INVALID_DESCRIPTOR;
		} else if (inPath == null) {
			setError("Access path is NULL");
			_errCode = ERR_CODES.INVALID_PATH;
		} else {
			cassandraDescriptorBean = inCassandraDescriptorBean;
			getLog().debug("Parsing access path: " + inPath);
			parseVirtualString(inPath);
		}
	}

	private void parseVirtualString(String inPath) {
		// * split access path to definitions
		pathDefs = inPath.split(PATH_DELIMETER);

		// * Validate path parts...
		if (pathDefs.length < 2 || pathDefs.length > PARTS.values().length) {
			setError("Cassandra access path definition length(tokens) is valid: " + inPath);
			_errCode = ERR_CODES.INVALID_PATH_STRUCTURE;
			return;
		}

		// * ... if OK, split access path to map values
		getLog().debug("Parsing access info for: " + inPath);
		for (int i = 0; i < pathDefs.length; i++) {
			_pathMap.put(PARTS.values()[i], pathDefs[i]);
			getLog().debug(
					String.format("%s. PathParts.%-7s = %s", i,
							PARTS.values()[i], pathDefs[i]));
		}

		// * [Mandatory] validate keyspace...
		getLog().debug("Validate for keyspace: " + getPathPart(PARTS.KSP));
		if (!cassandraDescriptorBean.containsKeyspace(getPathPart(PARTS.KSP))) {
			setError("Could not find keyspace: " + getPathPart(PARTS.KSP));
			_errCode = ERR_CODES.INVALID_KS;
			return;
		}

		KeyspaceBean ks = cassandraDescriptorBean
				.getKeyspace(getPathPart(PARTS.KSP));

		// * [Mandatory] validate column family...
		getLog().debug("Validate for column family: " + getPathPart(PARTS.CF));
		if (!ks.containsColumnFamily(getPathPart(PARTS.CF))) {
			setError("Could not find column family: " + getPathPart(PARTS.CF));
			_errCode = ERR_CODES.INVALID_CF;
			return;
		}

		// * defined just 2 parts
		if (_pathMap.size() == 2) {
			_resultType = RESULT_TYPES.DATA_KSP_CF;
			return;
		}

		// * [Optional] validate 3rd parts
		getLog().debug("Setup 3rd part of request: " + inPath);
		try {
			COLUMN_TYPES columnType = COLUMN_TYPES.valueOf(_pathMap
					.get(PARTS.SUPE_R));
			if (columnType == COLUMN_TYPES.COLUMNS) {
				_resultType = RESULT_TYPES.DATA_KSP_CF_COLUMNS;
			} else if (columnType == COLUMN_TYPES.LINKS) {
				_resultType = RESULT_TYPES.DATA_KSP_CF_LINKS;
			} else {
				setError("Invalid column type: " + columnType.toString());
				_resultType = RESULT_TYPES.UNDEFINED;
				_errCode = ERR_CODES.INVALID_DIC_TYPE;
				return;
			}				
		} catch (Exception e) {
			// ...we suppose that super is ID
			_resultType = RESULT_TYPES.DATA_KSP_CF_COLUMNS_SUPER;
		}

		// * defined just 3 parts
		if (_pathMap.size() == 3) {
			return;
		}

		ColumnFamilyBean cf = ks.getColumnFamilyByName(_pathMap.get(PARTS.CF));

		// * [Optional] validate 4th parts
		if (cf.containsColumnBeanByName(_pathMap.get(PARTS.COL))) {
			ColumnBean col = cf.getColumnByName(_pathMap.get(PARTS.COL));
			if (col.getTType() == COLUMN_TYPES.COLUMNS)
				_resultType = RESULT_TYPES.DATA_KSP_CF_COLUMNS_SUPER_COLUMN;
			else if(col.getTType() == COLUMN_TYPES.LINKS)
				_resultType = RESULT_TYPES.DATA_KSP_CF_LINKS_SUPER_COLUMN;
			else{
				setError(String.format("Invalid column type(%s) for column(%s)!",
						col.getType(),
						col.getName()));
				_resultType = RESULT_TYPES.UNDEFINED;
				_errCode = ERR_CODES.INVALID_DIC_TYPE;
				return;
			}
		} else {
			getLog().error("Invalid column name: " + _pathMap.get(PARTS.COL));
			_resultType = RESULT_TYPES.UNDEFINED;
			_errCode = ERR_CODES.INVALID_COLUMN;
			return;
		}
	}

	public String getPathPart(PARTS inPathPart) {
		return _pathMap.get(inPathPart);
	}

	public RESULT_TYPES getResultType() {
		return _resultType;
	}

	public EnumMap<PARTS, String> getPathMaps() {
		return _pathMap;
	}
}
