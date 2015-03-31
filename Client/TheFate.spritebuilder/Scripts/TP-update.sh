#!/bin/sh

#  Script.sh
#  TheFate
#
#  Created by Killua Liu on 10/28/14.
#  Copyright (c) 2014 Syzygy. All rights reserved.

#! /bin/sh
TP=/usr/local/bin/TexturePacker
if [ "${ACTION}" = "clean" ]
then
    # remove sheets - please add a matching expression here
    rm -f ${SRCROOT}/Source/Resources/Published-iOS/resources-phone/*.*
    rm -f ${SRCROOT}/Source/Resources/Published-iOS/resources-phonehd/*.*
    rm -f ${SRCROOT}/Source/Resources/Published-iOS/resources-tablet/*.*
    rm -f ${SRCROOT}/Source/Resources/Published-iOS/resources-tablethd/*.*
else
    # create all assets from tps files
    for file in `find "${SRCROOT}/Assets" -name "*.tps"`; do
        ${TP} "$file"
    done
fi
exit 0