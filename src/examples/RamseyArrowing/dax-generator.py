#!/usr/bin/env python

from Pegasus.DAX3 import *
import sys
import os
import re

n = 8
min_N = 0
max_N = 2 ** ((n * (n - 1)) / 2)
k = 2**17
base_dir = os.getcwd()
inputs_dir = base_dir + "/inputs"

# Create a abstract dag
dax = ADAG("ramsey-arrowing-test")

# notifcations on state changes for the dax
dax.invoke("all", "/usr/share/pegasus/notification/email")
	
# Add executables to the DAX-level replica catalog
wrapper = Executable(name="job-wrapper", arch="x86_64", installed=False)
wrapper.addPFN(PFN("file://" + base_dir + "/job-wrapper", "local"))
dax.addExecutable(wrapper)

# Gather all input files, and add them to the DAX-level replica catalog
in_files = []
for in_name in os.listdir(inputs_dir):
    in_file = File(in_name)
    in_file.addPFN(PFN("file://" + inputs_dir + "/" + in_name, "local"))
    dax.addFile(in_file)
    in_files.append(in_file)

# Set up each job to be run
for i in range(min_N, max_N + 1, k):

    # Determine bounds for the computation
    low = i
    high = i + k
    if high > max_N:
        high = max_N
        k = high - i
    if (i + k) > max_N:
        k = high - i

    # Create the first job
    job = Job(name="job-wrapper")
    job.addArguments(str(n), str(low), str(high))

    # Add the input file (in case there's more than one class file in the input directory)
    for c in in_files:
        job.uses(c, link=Link.INPUT)

    # Add the output file
    out_file = File("out_" + str(low) + "_" + str(high))
    job.uses(out_file, link=Link.OUTPUT)

    # Add the job!
    dax.addJob(job)

# Write the DAX to stdout
f = open("dax.xml", "w")
dax.writeXML(f)
f.close()

