
psql -c "drop database atomfeed"

psql -c "create database atomfeed"

psql -d "atomfeed" -c "drop schema if exists atomfeed cascade"

psql -d "atomfeed" -c "create schema atomfeed authorization pulkitb"

psql -d "atomfeed" -c "grant all on schema atomfeed to pulkitb"
