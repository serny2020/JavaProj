# Berkeley Map Web Server

## Overview

This project is the back-end implementation of a web server that supports displaying and interacting with a map similar to Google Maps. The project enables scrolling, zooming, and route finding using real-world data from OpenStreetMap (OSM). The front-end is provided, and this project focuses on back-end functionality.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

![Local GIF](./ScreenRecording2024-12-18at11.45.41AM-ezgif.com-video-to-gif-converter.gif)

## Application Structure

The project is divided into three main components:

1. **Rasterer**: Handles map rastering (converting map regions into images) based on user-defined latitude, longitude, and window dimensions.
2. **GraphDB**: Builds a graph structure from the OpenStreetMap dataset to represent intersections and roads.
3. **Router**: Implements route-finding using A* search to generate paths between specified start and end locations.

## Features

- **Map Rastering**: Converts user-defined map regions into a 2D array of map tiles (images) at appropriate resolutions based on the requested viewing area.
- **Graph Construction**: Parses OSM XML data and constructs a graph of nodes (intersections) and edges (roads).
- **Route Finding**: Uses the A* search algorithm to compute the shortest path between two points on the map.
- **Turn-by-Turn Navigation** (optional): Generates navigation directions based on the computed route.
- **Autocomplete & Search** (gold points): Supports location search and autocomplete for place names.

## How It Works

1. **URL-based Requests**:  
   The front-end sends requests via URLs containing parameters like latitude, longitude, width, and height. Example URL:  

2. **Rastering**:  
The `Rasterer` class processes the request and returns a 2D array of image filenames representing the appropriate map tiles.

3. **GraphDB**:  
The `GraphDB` class builds a graph from the provided OSM dataset.

4. **Routing**:  
The `Router` class finds the shortest path between the specified start and end points using A* search.

## Getting Started

### Prerequisites

- **Java 8** or higher