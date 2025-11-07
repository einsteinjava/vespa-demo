# Vespa Demo - Album Recommendations

A simple Vespa application for music album search and recommendations. This demo demonstrates how to deploy a Vespa application locally, feed music data, and perform searches.

## Overview

This application indexes music albums with metadata including:
- Artist name
- Album title
- Release year
- Category scores (pop, rock, jazz)

The application includes a ranking profile that combines BM25 text matching with personalized recommendations based on user category preferences.

## Requirements

- Docker installed and running
- Minimum 4GB RAM allocated to Docker (check with `docker info | grep "Total Memory"`)
- Vespa CLI installed ([Installation guide](https://docs.vespa.ai/en/vespa-cli.html))

## Quick Start

### 1. Start Vespa Container

```bash
docker run --detach --name vespa --hostname vespa-container \
  --publish 8080:8080 --publish 19071:19071 \
  vespaengine/vespa
```

### 2. Configure Vespa CLI

```bash
vespa config set target local
```

### 3. Deploy the Application

From the project root directory:

```bash
vespa deploy --wait 300 ./app
```

Wait for deployment to complete (up to 5 minutes). The application includes a disk limit configuration set to 85% to handle systems with limited disk space.

### 4. Feed Sample Data

```bash
vespa feed dataset/documents.jsonl
```

This will index 5 sample albums:
- A Head Full of Dreams (Coldplay)
- Hardwired...To Self-Destruct (Metallica)
- Liebe ist für alle da (Rammstein)
- Love Is Here To Stay (Diana Krall)
- When We All Fall Asleep, Where Do We Go? (Billie Eilish)

### 5. Query the Data

**Search for albums:**
```bash
vespa query "select * from music where album contains 'head'"
```

**Get a specific document:**
```bash
vespa document get id:mynamespace:music::a-head-full-of-dreams
```

**Search by artist:**
```bash
vespa query "select * from music where artist contains 'Coldplay'"
```

## Project Structure

```
vespa-demo/
├── app/
│   ├── schemas/
│   │   └── music.sd          # Document schema definition
│   └── services.xml          # Application services configuration
├── dataset/
│   ├── documents.jsonl       # Sample data in JSONL format
│   └── *.json                # Individual album JSON files
└── README.md
```

## Configuration

### Schema (`app/schemas/music.sd`)

Defines the document structure:
- `artist`: String field (indexed and searchable)
- `album`: String field (indexed with BM25)
- `year`: Integer field
- `category_scores`: Tensor with category preferences (pop, rock, jazz)

### Services (`app/services.xml`)

- **Container cluster**: Handles queries and document API requests
- **Content cluster**: Stores and indexes documents
- **Resource limits**: Disk limit set to 85% (configured in `<tuning>` section)

## Troubleshooting

### Feed Blocked (507 Error)

If you encounter a `507` error with message "disk on node 0 is X% full", the disk limit has been configured to 85% in `services.xml`. If you still see this error:

1. Check available disk space: `docker exec vespa df -h`
2. Free up space on your host system
3. The limit can be adjusted in `app/services.xml` under `<tuning><resource-limits><disk>`

### Deployment Issues

- Ensure Docker has at least 4GB RAM allocated
- Wait for deployment to complete (check with `vespa status`)
- Verify container is running: `docker ps | grep vespa`

## Access Points

Once deployed, you can access:
- **Query API**: http://localhost:8080
- **Config Server**: http://localhost:19071

## Cleanup

To stop and remove the Vespa container:

```bash
docker rm -f vespa
```

## Learn More

- [Vespa Documentation](https://docs.vespa.ai/)
- [Vespa Getting Started](https://cloud.vespa.ai/en/getting-started)
- [Vespa Query Language](https://docs.vespa.ai/en/query-language.html)
