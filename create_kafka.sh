#!/bin/bash
docker exec broker \
kafka-topics --bootstrap-server broker:9092 \
             --create \
             --topic tweets \
             --partitions 4