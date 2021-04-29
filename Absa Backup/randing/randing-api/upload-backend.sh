#! /bin/sh

sshpass -p $FTP_PWD ssh -o StrictHostKeyChecking=no $FTP_USER@$FTP_ADDR  'pwd; pkill java; nohup java -jar debty-0.0.1-SNAPSHOT.jar &'
