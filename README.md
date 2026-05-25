# Distributed File System - DataNode

DataNode is a storage server responsible for persisting file chunks on disk and serving chunk-related operations within the Distributed File System (DFS).

## Responsibilities

- Store incoming file chunks
- Retrieve chunks on request
- Delete stored chunks
- Manage local disk storage
- Expose REST APIs for chunk operations


## Configuration

`application.properties`

```properties
server.port=8081
storage.base-path=D:/DiskShared/folderName
```

| Property | Description |
|-----------|------------|
| `server.port` | DataNode server port |
| `storage.node-id` | Unique identifier of the node |
| `storage.base-path` | Local directory for chunk storage |

## APIs

### Upload Chunk

```http
POST /api/v1/blocks/{blockId}
```

Stores a chunk on local disk.

### Download Chunk

```http
GET /api/v1/blocks/{blockId}
```

Returns the requested chunk.

### Delete Chunk

```http
DELETE /api/v1/blocks/{blockId}
```

Removes the chunk from storage.

## Storage Layout

```text
storage/
├── chunk-001
├── chunk-002
├── chunk-003
└── ...
```

Each chunk is stored as an individual file on disk.

## Tech Stack

- Java
- Spring Boot
- Maven
- REST APIs
- Java NIO File System

## Future Improvements

- Chunk replication
- Heartbeat mechanism
- DataNode registration
- Failure recovery
- Checksum validation
- Compression support