# Complete Setup Guide for Vespa RAG Demo

This guide will walk you through setting up and running the Vespa RAG Demo project from scratch, even if you have no prior experience with software development. We'll explain everything step by step.

## Table of Contents

1. [What You'll Need](#what-youll-need)
2. [Installing Docker](#installing-docker)
3. [Installing Vespa CLI](#installing-vespa-cli)
4. [Installing Make](#installing-make)
5. [Getting a Gemini API Key](#getting-a-gemini-api-key)
6. [Setting Up the Project](#setting-up-the-project)
7. [Running the Application](#running-the-application)
8. [Testing the Application](#testing-the-application)
9. [Troubleshooting](#troubleshooting)

---

## What You'll Need

Before we start, you'll need:

- **A computer** running Linux, macOS, or Windows
- **An internet connection** to download software and data
- **About 30-60 minutes** for the complete setup
- **At least 8GB of RAM** (16GB recommended)
- **At least 10GB of free disk space**

---

## Installing Docker

Docker is a tool that allows us to run applications in isolated containers. Think of it like a shipping container for software - it packages everything needed to run an application so it works the same way on any computer.

### For Linux (Ubuntu/Debian)

1. **Open a terminal** (press `Ctrl + Alt + T` or search for "Terminal" in your applications)

2. **Update your system packages:**
   ```bash
   sudo apt-get update
   ```

3. **Install required packages:**
   ```bash
   sudo apt-get install -y ca-certificates curl gnupg lsb-release
   ```

4. **Add Docker's official GPG key:**
   ```bash
   sudo mkdir -p /etc/apt/keyrings
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
   ```

5. **Set up the Docker repository:**
   ```bash
   echo \
     "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
     $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
   ```

6. **Install Docker:**
   ```bash
   sudo apt-get update
   sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
   ```

7. **Add your user to the docker group** (so you don't need sudo for every command):
   ```bash
   sudo usermod -aG docker $USER
   ```

8. **Log out and log back in** for the changes to take effect, or run:
   ```bash
   newgrp docker
   ```

9. **Verify Docker is installed:**
   ```bash
   docker --version
   docker compose version
   ```
   You should see version numbers printed.

### For macOS

1. **Download Docker Desktop for Mac:**
   - Go to: https://www.docker.com/products/docker-desktop/
   - Click "Download for Mac"
   - Choose the version for your Mac (Intel or Apple Silicon)

2. **Install Docker Desktop:**
   - Open the downloaded `.dmg` file
   - Drag Docker to your Applications folder
   - Open Docker from Applications
   - Follow the setup wizard
   - You may need to enter your password

3. **Verify Docker is installed:**
   - Open Terminal (Applications > Utilities > Terminal)
   - Run:
     ```bash
     docker --version
     docker compose version
     ```

### For Windows

1. **Download Docker Desktop for Windows:**
   - Go to: https://www.docker.com/products/docker-desktop/
   - Click "Download for Windows"
   - Download the installer

2. **Install Docker Desktop:**
   - Run the installer
   - Make sure "Use WSL 2 instead of Hyper-V" is checked (if available)
   - Follow the installation wizard
   - Restart your computer when prompted

3. **Start Docker Desktop:**
   - Find Docker Desktop in your Start menu and launch it
   - Wait for it to start (you'll see a whale icon in your system tray)

4. **Verify Docker is installed:**
   - Open PowerShell or Command Prompt
   - Run:
     ```bash
     docker --version
     docker compose version
     ```

---

## Installing Vespa CLI

Vespa CLI is a command-line tool that helps us interact with Vespa (the search engine we're using). It's like a remote control for Vespa. We need it to deploy the Vespa application schema and feed data to Vespa.

### For Linux and macOS

1. **Open a terminal**
   - On Linux: Press `Ctrl + Alt + T` or search for "Terminal"
   - On macOS: Open Terminal from Applications > Utilities

2. **Check if curl is installed:**
   ```bash
   curl --version
   ```
   If you see a version number, you're good. If you get an error, install curl first:
   - **Linux (Ubuntu/Debian):** `sudo apt-get install curl`
   - **macOS:** curl is usually pre-installed, but if not: `brew install curl`

3. **Download and install Vespa CLI:**
   ```bash
   curl -fsSL https://get.vespa.ai | bash
   ```
   
   **What this does:**
   - Downloads the official Vespa CLI installer script
   - Runs it automatically to install Vespa CLI
   - Installs it to `~/.local/bin/vespa` (in your home directory)
   
   **Expected output:**
   You should see messages about downloading and installing Vespa CLI. The installation usually takes 10-30 seconds.

4. **Add Vespa to your PATH** (so you can run `vespa` from anywhere):
   
   **What is PATH?**
   PATH is a list of directories where your computer looks for programs. By adding Vespa to PATH, you can type `vespa` from any folder and it will work.
   
   **Temporary (for current terminal session only):**
   ```bash
   export PATH="$HOME/.local/bin:$PATH"
   ```
   
   **Permanent (so it works in all new terminals):**
   ```bash
   echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
   source ~/.bashrc
   ```
   
   **Note for macOS users:**
   - If you're using zsh (default on newer macOS), use `~/.zshrc` instead of `~/.bashrc`:
     ```bash
     echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.zshrc
     source ~/.zshrc
     ```
   - To check which shell you're using: `echo $SHELL`

5. **Verify Vespa CLI is installed:**
   ```bash
   vespa version
   ```
   You should see a version number like `vespa version 8.x.x` or similar.
   
   **If you get "command not found":**
   - Make sure you ran the `export PATH` command (step 4)
   - Try closing and reopening your terminal
   - Check if Vespa was installed: `ls ~/.local/bin/vespa`
   - If the file exists, make sure you added it to PATH correctly

### For Windows

1. **Open a web browser**

2. **Download Vespa CLI:**
   - Go to: https://github.com/vespa-engine/vespa/releases
   - Look for the latest release (usually at the top)
   - Scroll down to "Assets" section
   - Download the file named `vespa-cli-windows-amd64.zip`
   - **Note:** If you have a 32-bit Windows system, look for `vespa-cli-windows-386.zip` instead

3. **Extract the ZIP file:**
   - Right-click on the downloaded ZIP file
   - Select "Extract All..." or "Extract to..."
   - Choose a location like `C:\vespa-cli` or `C:\Program Files\vespa-cli`
   - Click "Extract"
   - You should see a file named `vespa.exe` in the extracted folder

4. **Add Vespa to your PATH:**
   
   **What is PATH?**
   PATH is a list of folders where Windows looks for programs. By adding Vespa to PATH, you can type `vespa` from any folder and it will work.
   
   **Steps:**
   - Press `Win + X` (Windows key + X)
   - Select "System" from the menu
   - Click "Advanced system settings" on the right side
   - Click the "Environment Variables" button at the bottom
   - In the "System variables" section (bottom half), find "Path" and click "Edit"
   - Click "New" button
   - Type the path where you extracted Vespa (e.g., `C:\vespa-cli`)
   - Click "OK" on all open dialogs
   - **Important:** Close and reopen your PowerShell or Command Prompt for changes to take effect

5. **Verify Vespa CLI is installed:**
   - **Close your current PowerShell/Command Prompt window**
   - Open a **new** PowerShell or Command Prompt window
   - Navigate to any folder (e.g., `cd C:\Users\YourName`)
   - Run:
     ```bash
     vespa version
     ```
   - You should see a version number like `vespa version 8.x.x` or similar
   
   **If you get "command not found":**
   - Make sure you closed and reopened your terminal after adding to PATH
   - Verify the path you added is correct (check the folder exists)
   - Make sure you added it to "System variables" > "Path", not "User variables"
   - Try typing the full path: `C:\vespa-cli\vespa.exe version` (replace with your actual path)

### Alternative Installation Methods

**For Linux (if the script doesn't work):**

You can also install Vespa CLI manually:

1. **Download the binary:**
   ```bash
   # For 64-bit Linux
   curl -L -o vespa.tar.gz https://github.com/vespa-engine/vespa/releases/latest/download/vespa-cli-linux-amd64.tar.gz
   
   # Extract it
   tar -xzf vespa.tar.gz
   
   # Move to a location in your PATH
   sudo mv vespa /usr/local/bin/vespa
   
   # Make it executable
   sudo chmod +x /usr/local/bin/vespa
   ```

2. **Verify:**
   ```bash
   vespa version
   ```

**For macOS (if the script doesn't work):**

Similar to Linux, you can download and install manually:

1. **Download the binary:**
   ```bash
   # For Apple Silicon (M1/M2/M3 Macs)
   curl -L -o vespa.tar.gz https://github.com/vespa-engine/vespa/releases/latest/download/vespa-cli-darwin-arm64.tar.gz
   
   # For Intel Macs
   curl -L -o vespa.tar.gz https://github.com/vespa-engine/vespa/releases/latest/download/vespa-cli-darwin-amd64.tar.gz
   
   # Extract it
   tar -xzf vespa.tar.gz
   
   # Move to a location in your PATH
   sudo mv vespa /usr/local/bin/vespa
   
   # Make it executable
   sudo chmod +x /usr/local/bin/vespa
   ```

2. **Verify:**
   ```bash
   vespa version
   ```

---

## Installing Make

`make` is a build automation tool that helps us run commands easily. The project uses a `Makefile` which contains shortcuts for common tasks. Think of it like a recipe book - instead of typing long commands, we can just type `make quickstart` and it does everything for us.

### For Linux (Ubuntu/Debian)

1. **Open a terminal**

2. **Install make:**
   ```bash
   sudo apt-get update
   sudo apt-get install -y make
   ```

3. **Verify make is installed:**
   ```bash
   make --version
   ```
   You should see version information printed.

### For macOS

1. **Open Terminal** (Applications > Utilities > Terminal)

2. **Install Xcode Command Line Tools** (which includes make):
   ```bash
   xcode-select --install
   ```
   - A popup window will appear asking if you want to install the tools
   - Click "Install" and wait for the installation to complete
   - This may take 10-15 minutes

3. **Verify make is installed:**
   ```bash
   make --version
   ```
   You should see version information printed.

**Alternative for macOS (if you have Homebrew):**
If you already have Homebrew installed, you can also install make with:
```bash
brew install make
```

### For Windows

Windows doesn't have `make` by default, but you have a few options:

**Option A: Use Git Bash (Recommended)**
1. **Install Git for Windows:**
   - Download from: https://git-scm.com/download/win
   - During installation, make sure to select "Git Bash Here" option
   - Git Bash includes `make` and other Unix-like tools

2. **Use Git Bash instead of PowerShell:**
   - Right-click in the project folder
   - Select "Git Bash Here"
   - All the `make` commands will work in Git Bash

**Option B: Install Make for Windows**
1. **Download Make for Windows:**
   - Go to: http://gnuwin32.sourceforge.net/packages/make.htm
   - Download the "Complete package, except sources" installer
   - Run the installer

2. **Add to PATH:**
   - The installer usually adds it automatically, but if not:
   - Add `C:\Program Files (x86)\GnuWin32\bin` to your system PATH
   - (See the Vespa CLI section for instructions on adding to PATH)

3. **Verify make is installed:**
   - Open a new Command Prompt or PowerShell
   - Run:
     ```bash
     make --version
     ```

**Option C: Use Docker Compose Commands Directly**
If you prefer not to install make, you can use `docker compose` commands directly instead of `make` commands. See the [Alternative Commands Without Make](#alternative-commands-without-make) section below.

---

## Getting a Gemini API Key

The application uses Google's Gemini AI to generate responses. You'll need a free API key from Google.

1. **Go to Google AI Studio:**
   - Open your web browser
   - Visit: https://aistudio.google.com/

2. **Sign in:**
   - Click "Sign in" in the top right
   - Use your Google account (Gmail account works)

3. **Get your API key:**
   - Once signed in, you should see "Get API key" or a similar option
   - Click "Get API key" or "Create API key"
   - If prompted, create a new Google Cloud project (you can use the default name)
   - Copy the API key that appears (it will look like: `AIzaSy...` - a long string of letters and numbers)
   - **Important:** Save this key somewhere safe - you'll need it in the next step!

---

## Setting Up the Project

Now that you have all the required tools installed, let's set up the project itself.

### Step 1: Download or Clone the Project

If you haven't already, you need to get the project files on your computer.

**Option A: If you have the project files in a folder:**
- Navigate to that folder in your terminal:
  ```bash
  cd /path/to/vespa-demo
  ```
  (Replace `/path/to/vespa-demo` with the actual path to your project folder)

**Option B: If you need to download the project:**
- If this is a Git repository, you can clone it:
  ```bash
  git clone <repository-url>
  cd vespa-demo
  ```

### Step 2: Create the Environment File

The application needs your Gemini API key to work. We'll store it in a special file called `.env`.

1. **Navigate to the project folder** (if you're not already there):
   ```bash
   cd vespa-demo
   ```

2. **Copy the example environment file:**
   ```bash
   cp .env.example .env
   ```
   
   On Windows (PowerShell):
   ```powershell
   Copy-Item .env.example .env
   ```

3. **Open the `.env` file in a text editor:**
   - On Linux: `nano .env` or `gedit .env`
   - On macOS: `open -e .env` or `nano .env`
   - On Windows: `notepad .env`

4. **Replace the placeholder with your actual API key:**
   - Find the line that says: `GEMINI_API_KEY=your_gemini_api_key_here`
   - Replace `your_gemini_api_key_here` with the API key you copied from Google AI Studio
   - It should look like: `GEMINI_API_KEY=AIzaSy...` (your actual key)
   - Save the file and close the editor

### Step 3: Verify Your Setup

Before running the application, let's make sure everything is ready:

1. **Check Docker is running:**
   ```bash
   docker ps
   ```
   This should show either an empty list or some containers. If you get an error, Docker might not be running.

2. **Check Vespa CLI is available:**
   ```bash
   vespa version
   ```
   This should show a version number.

3. **Check make is available:**
   ```bash
   make --version
   ```
   This should show version information.

4. **Check your `.env` file exists:**
   ```bash
   ls -la .env
   ```
   (On Windows: `dir .env`)

---

## Running the Application

Now comes the exciting part - running the application! We'll use a single command that does everything for you.

### Quick Start (Recommended)

The easiest way to get everything running is to use the `quickstart` command, which will:
- Build the Docker images (create the containers)
- Start all services (Vespa and the RAG application)
- Deploy the Vespa schema (configure Vespa)
- Load sample data (music albums)

1. **Make sure you're in the project directory:**
   ```bash
   cd vespa-demo
   ```

2. **Run the quickstart command:**
   ```bash
   make quickstart
   ```

   **What this does:**
   - First, it runs `make up` which starts Docker containers
   - Then it waits 60 seconds for Vespa to be ready
   - Deploys the Vespa application schema
   - Feeds sample data to Vespa
   - Shows you completion messages

3. **Wait for completion:**
   - This process takes about 2-5 minutes
   - You'll see various messages as things start up
   - When you see "=== Quick Start Complete! ===", you're done!

### Manual Steps (If Quickstart Doesn't Work)

If the quickstart command doesn't work, you can do the steps manually:

1. **Build the Docker images:**
   ```bash
   make docker-build
   ```

2. **Start the services:**
   ```bash
   make docker-up
   ```

3. **Wait for Vespa to be ready** (about 60 seconds), then deploy:
   ```bash
   make vespa-deploy
   ```

4. **Feed the sample data:**
   ```bash
   make vespa-feed
   ```

### What's Running Now?

After the quickstart completes, you have:
- **Vespa** running on: http://localhost:8080
- **RAG Application** running on: http://localhost:8081

You can open these URLs in your web browser, though they won't show much without making API calls.

---

## Testing the Application

Now let's test that everything is working correctly!

### Test 1: Check Health Status

First, let's make sure all services are healthy:

```bash
make health
```

You should see:
- Vespa Health: `up` or similar
- RAG App Search Health: A response indicating it's working
- RAG App RAG Health: A response indicating it's working

### Test 2: Test the Search Endpoint

This tests the basic search functionality:

```bash
make test-search
```

**What this does:**
- Sends a search query for "rock music"
- Returns up to 3 matching albums
- Displays the results in JSON format

**Expected output:**
You should see JSON data with music albums that match "rock music", including fields like:
- `title`: Album title
- `artist`: Artist name
- `genre`: Music genre
- `year`: Release year
- `description`: Album description

### Test 3: Test the RAG Endpoint

This tests the AI-powered question answering:

```bash
make test-rag
```

**What this does:**
- Asks the AI: "What are some good rock albums?"
- The system searches for relevant albums
- Uses the search results to generate an AI response
- Returns both the search results and the AI-generated answer

**Expected output:**
You should see JSON data with:
- `query`: Your question
- `results`: The albums found
- `answer`: An AI-generated response based on the albums

### Understanding the Results

The JSON output might look complex, but here's what to look for:

- **Search results** show albums with titles, artists, and descriptions
- **RAG answer** is a natural language response generated by AI
- If you see errors, check the [Troubleshooting](#troubleshooting) section

---

## Using the Application

### Making Your Own Queries

You can test the application with your own queries using `curl` commands.

**Search for albums:**
```bash
curl -X POST http://localhost:8081/api/search \
  -H "Content-Type: application/json" \
  -d '{"query": "jazz music", "maxResults": 5, "searchMode": "hybrid"}' | jq '.'
```

**Ask a question:**
```bash
curl -X POST http://localhost:8081/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"query": "What are the best albums from the 1980s?", "maxResults": 5, "searchMode": "hybrid"}' | jq '.'
```

Replace the query text with whatever you want to search for or ask about!

---

## Useful Commands

Here are some helpful commands you might need:

### Viewing Logs

**See all logs:**
```bash
make logs
```

**See only Vespa logs:**
```bash
make logs-vespa
```

**See only application logs:**
```bash
make logs-app
```

Press `Ctrl + C` to stop viewing logs.

### Stopping the Application

**Stop all services:**
```bash
make docker-down
```

### Restarting the Application

**Restart everything:**
```bash
make docker-restart
```

### Cleaning Up

**Stop services and remove all data:**
```bash
make docker-clean
```

**Warning:** This will delete all data! You'll need to run `make quickstart` again afterward.

### Alternative Commands Without Make

If you don't have `make` installed or prefer to use `docker compose` commands directly, here are the equivalent commands:

**Instead of `make quickstart`:**
```bash
docker compose build
docker compose up -d
sleep 60
vespa config set target local
vespa deploy --wait 300 ./app
vespa feed dataset/documents.jsonl
```

**Instead of `make docker-build`:**
```bash
docker compose build
```

**Instead of `make docker-up`:**
```bash
docker compose up -d
```

**Instead of `make docker-down`:**
```bash
docker compose down
```

**Instead of `make docker-restart`:**
```bash
docker compose down
docker compose up -d
```

**Instead of `make logs`:**
```bash
docker compose logs -f
```

**Instead of `make logs-vespa`:**
```bash
docker compose logs -f vespa
```

**Instead of `make logs-app`:**
```bash
docker compose logs -f rag-app
```

**Instead of `make vespa-deploy`:**
```bash
vespa config set target local
vespa deploy --wait 300 ./app
```

**Instead of `make vespa-feed`:**
```bash
vespa feed dataset/documents.jsonl
```

**Instead of `make health`:**
```bash
curl -s http://localhost:19071/state/v1/health | jq -r '.status.code'
curl -s http://localhost:8081/api/search/health
curl -s http://localhost:8081/api/rag/health
```

**Instead of `make test-search`:**
```bash
curl -X POST http://localhost:8081/api/search \
  -H "Content-Type: application/json" \
  -d '{"query": "rock music", "maxResults": 3, "searchMode": "hybrid"}' | jq '.'
```

**Instead of `make test-rag`:**
```bash
curl -X POST http://localhost:8081/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"query": "What are some good rock albums?", "maxResults": 3, "searchMode": "hybrid"}' | jq '.'
```

---

## Troubleshooting

### Problem: "Docker command not found"

**Solution:**
- Make sure Docker is installed (see [Installing Docker](#installing-docker))
- On Linux, make sure you've logged out and back in after adding your user to the docker group
- Try running `sudo docker` instead (Linux only)

### Problem: "Vespa command not found" or "vespa: command not found"

**Solution:**
- Make sure Vespa CLI is installed (see [Installing Vespa CLI](#installing-vespa-cli))
- **On Linux/macOS:**
  - Make sure you added Vespa to your PATH (see step 4 in the installation section)
  - Try running: `~/.local/bin/vespa version` to test if it's installed
  - If that works, you need to add it to PATH: `export PATH="$HOME/.local/bin:$PATH"`
  - Make it permanent by adding to `~/.bashrc` or `~/.zshrc`
  - Close and reopen your terminal after updating PATH
- **On Windows:**
  - Make sure you added the Vespa folder to your system PATH
  - Close and reopen your PowerShell/Command Prompt after adding to PATH
  - Try running the full path: `C:\vespa-cli\vespa.exe version` (replace with your actual path)
  - Verify the path in Environment Variables is correct
- **General:**
  - Check if the vespa executable exists in the installation directory
  - Make sure you're using a new terminal window after installation

### Problem: "make: command not found"

**Solution:**
- See the [Installing Make](#installing-make) section above for detailed installation instructions
- On Linux: `sudo apt-get install make`
- On macOS: Install Xcode Command Line Tools: `xcode-select --install`
- On Windows: Use Git Bash (which includes make) or use `docker compose` commands directly (see [Alternative Commands Without Make](#alternative-commands-without-make) section)

### Problem: "Port already in use" or "Address already in use"

**Solution:**
- Another application is using ports 8080 or 8081
- Stop the other application, or
- Change the ports in `docker-compose.yml` (advanced)

### Problem: "Vespa not ready" or health checks failing

**Solution:**
- Wait longer (Vespa can take 2-3 minutes to start)
- Check Vespa logs: `make logs-vespa`
- Make sure you have enough RAM (at least 4GB free)
- Try restarting: `make docker-restart`

### Problem: "API key invalid" or Gemini errors

**Solution:**
- Check your `.env` file has the correct API key
- Make sure there are no extra spaces in the API key
- Verify your API key at https://aistudio.google.com/
- Make sure you haven't exceeded API rate limits

### Problem: "Cannot connect to Docker daemon"

**Solution:**
- Make sure Docker Desktop (macOS/Windows) is running
- On Linux, start Docker: `sudo systemctl start docker`
- Check Docker is running: `docker ps`

### Problem: Application starts but test commands fail

**Solution:**
- Wait a bit longer (the app might still be starting)
- Check health: `make health`
- Check logs: `make logs-app`
- Make sure your `.env` file is correct

### Problem: "jq: command not found"

**Solution:**
- Install jq (a JSON formatter):
  - Linux: `sudo apt-get install jq`
  - macOS: `brew install jq`
  - Windows: Download from https://stedolan.github.io/jq/download/
- Or remove `| jq '.'` from the test commands in the Makefile

### Problem: "failed to solve: failed to compute cache key" or "/gradle: not found" during Docker build

**Solution:**
- This error occurs when Docker can't find the `gradle` directory during the build
- If the directory is missing, you may need to regenerate it or restore it from version control by using command `git pull` from the terminal on that working directory.
- Also check for any warnings about `version: '3.8'` in docker-compose.yml - this field is obsolete in newer Docker Compose versions and can be safely removed

### Getting More Help

If you're still stuck:
1. Check the logs: `make logs`
2. Check the health status: `make health`
3. Review the README.md for more information
4. Check that all prerequisites are installed correctly

---

## Next Steps

Congratulations! You've successfully set up and run the Vespa RAG Demo. Here's what you can do next:

1. **Explore the code:** Look at the files in the `src/` directory to understand how it works
2. **Modify queries:** Try different search queries and questions
3. **Add your own data:** Replace `dataset/documents.jsonl` with your own data
4. **Read the API documentation:** Check `API.md` for detailed API information
5. **Experiment:** Try different search modes and parameters

---

## Summary

You've learned how to:
- Install Docker and Docker Compose
- Install Vespa CLI
- Install Make (or use alternative commands)
- Get a Gemini API key
- Set up the project
- Run the application
- Test the search and RAG endpoints

The application is now running and ready to use! You can search for music albums and ask questions that will be answered using AI-powered retrieval-augmented generation.

---

## Quick Reference

**Start everything:**
```bash
make quickstart
```

**Check health:**
```bash
make health
```

**Test search:**
```bash
make test-search
```

**Test RAG:**
```bash
make test-rag
```

**View logs:**
```bash
make logs
```

**Stop everything:**
```bash
make docker-down
```

**Get help:**
```bash
make help
```

