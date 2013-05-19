#! /bin/sh

psql -c "drop database atomfeed" -U postgres

psql -c "create database atomfeed" -U postgres

psql -d "atomfeed" -c "create schema atomfeed authorization postgres" -U postgres

psql -d "atomfeed" -c "grant all on schema atomfeed to postgres" -U postgres
