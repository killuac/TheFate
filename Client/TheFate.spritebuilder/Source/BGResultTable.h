//
//  BGResultTable.h
//  TheFate
//
//  Created by Killua Liu on 4/6/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGGameOverCell.h"
#import "BGGameResult.h"

#define ROW_HEIGHT  24

@interface BGResultTable : CCNode <CCTableViewDataSource>

+ (id)resultTableWithGameResults:(NSArray *)gameResults;

@end
