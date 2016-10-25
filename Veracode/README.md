# Crawler

## Purpose: 
  This crawler is meant to find all links given a seed link. Report errors if any.

## Configurations: 
  Crawler can be configured by editing *"Constants.java"* file located in edu.veracode.crawler. 
### Properites: 
* MAX_NO_THREADS : Allows you to choose multi threading mode and configure number of threads 
* DEQUEUE_SIZE   : Allows you to choose how many links a thread should work on at a time 
* MAX_URLS : Limits the number of URLs that should be crawled 
* SEED_URL : Is the seed url to start with

## Execution Instructions: 
  To execute the crawler run the file *"Executor.java"* located in edu.veracode.crawler.
