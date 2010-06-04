package org.hydra.db.server;

import java.util.EnumMap;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.utils.abstracts.ALogger;

public class CassandraVirtualPath extends ALogger {
	// Access path beans - needs for "same time initialization"
	public KeyspaceBean kspBean = null;
	public ColumnFamilyBean cfBean = null;
	public ColumnBean colBean = null;
	
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

	ERR_CODES _errCode = ERR_CODES.UNDEFINED;
	
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
		KSP, CF, /* KEY, - unnecessary */SUPER, COL
	};

	// Result types
	public enum PATH_TYPE {
		UNDEFINED, 
		KSP___CF___,			 			/* get IDs */
		KSP___CF___COLUMNS, 				/* get column descriptions */
		KSP___CF___COLUMNS___SUPER, 		/* get columns for certain ID (super) */
		KSP___CF___COLUMNS___SUPER___COL,	/* get certain column from columns for certain ID (super) */
		KSP___CF___LINKS, 					/* get links descriptions */
		KSP___CF___LINKS___SUPER___COL,		/* get linked ID's for certain ID (super) */ 
	};

	PATH_TYPE _resultType = PATH_TYPE.UNDEFINED;

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
		kspBean = cassandraDescriptorBean
				.getKeyspace(getPathPart(PARTS.KSP));

		// * [Mandatory] validate column family...
		getLog().debug("Validate for column family: " + getPathPart(PARTS.CF));
		if (!kspBean.containsColumnFamily(getPathPart(PARTS.CF))) {
			setError("Could not find column family: " + getPathPart(PARTS.CF));
			_errCode = ERR_CODES.INVALID_CF;
			return;
		}
		cfBean = kspBean.getColumnFamilyByName(_pathMap.get(PARTS.CF));

		// * defined just 2 parts
		if (_pathMap.size() == 2) {
			_errCode = ERR_CODES.NO_ERROR;
			_resultType = PATH_TYPE.KSP___CF___;
			return;
		}

		// * [Optional] validate 3rd parts
		getLog().debug("Setup 3rd part of request: " + inPath);
		try {
			COLUMN_TYPES columnType = COLUMN_TYPES.valueOf(_pathMap
					.get(PARTS.SUPER));
			if (columnType == COLUMN_TYPES.COLUMNS) {
				_resultType = PATH_TYPE.KSP___CF___COLUMNS;
			} else if (columnType == COLUMN_TYPES.LINKS) {
				_resultType = PATH_TYPE.KSP___CF___LINKS;
			} else {
				setError("Invalid column type: " + columnType.toString());
				_resultType = PATH_TYPE.UNDEFINED;
				_errCode = ERR_CODES.INVALID_DIC_TYPE;
				return;
			}				
		} catch (Exception e) {
			// ...we suppose that SUPER is actual ID
			_errCode = ERR_CODES.NO_ERROR;
			_resultType = PATH_TYPE.KSP___CF___COLUMNS___SUPER;
		}

		// * defined just 3 parts
		if (_pathMap.size() == 3) {
			_errCode = ERR_CODES.NO_ERROR;
			return;
		}

		// * [Optional] validate 4th parts
		if (cfBean.getColumns().containsKey(_pathMap.get(PARTS.COL))) {
			colBean = cfBean.getColumns().get(_pathMap.get(PARTS.COL));
			_resultType = PATH_TYPE.KSP___CF___COLUMNS___SUPER___COL;
		}else if(cfBean.getLinks().containsKey(_pathMap.get(PARTS.COL))){
			colBean = cfBean.getLinks().get(_pathMap.get(PARTS.COL));
			_resultType = PATH_TYPE.KSP___CF___LINKS___SUPER___COL;
			
		}else{
			setError(String.format("Invalid column (%s)!",
					_pathMap.get(PARTS.COL)));
			_resultType = PATH_TYPE.UNDEFINED;
			_errCode = ERR_CODES.INVALID_COLUMN;
			return;
			
		}
		
		_errCode = ERR_CODES.NO_ERROR;
		return;
	}

	public String getPathPart(PARTS inPathPart) {
		return _pathMap.get(inPathPart);
	}

	public PATH_TYPE getPathType() {
		return _resultType;
	}

	public EnumMap<PARTS, String> getPathMaps() {
		return _pathMap;
	}
}
