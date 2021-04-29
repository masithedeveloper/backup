#! /bin/sh

cd ../debty-frontend
npm install
npm run build
aws s3 sync ./dist s3://debty-frontend
