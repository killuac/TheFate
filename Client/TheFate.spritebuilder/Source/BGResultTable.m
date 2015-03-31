//
//  BGResultTable.m
//  TheFate
//
//  Created by Killua Liu on 4/6/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGResultTable.h"


@implementation BGResultTable {
    NSArray *_gameResults;
}

+ (id)resultTableWithGameResults:(NSArray *)gameResults
{
    return [[self alloc] initWithGameResults:gameResults];
}

- (id)initWithGameResults:(NSArray *)gameResults
{
    if (self = [super init]) {
        _gameResults = gameResults;
    }
    return self;
}

- (NSUInteger)tableViewNumberOfRows:(CCTableView *)tableView
{
    return _gameResults.count;
}

- (float)tableView:(CCTableView*)tableView heightForRowAtIndex:(NSUInteger) index
{
    return ROW_HEIGHT;
}

- (CCTableViewCell *)tableView:(CCTableView *)tableView nodeForRowAtIndex:(NSUInteger)index
{
    BGGameOverCell *cell = (BGGameOverCell *)[CCBReader load:kCcbiGameOverCell];
    BGGameResult *gameResult = _gameResults[index];
    cell.nickNameLabel.string = gameResult.nickName;
    cell.heroNameLabel.string = gameResult.heroName;
    cell.factionSprite.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:gameResult.roleImageName];
    
    if (gameResult.isEscaped) {
        cell.aliveStatusLabel.fontColor = [CCColor redColor];
    } else {
        cell.aliveStatusLabel.fontColor = (gameResult.isAlive) ? [CCColor greenColor] : [CCColor grayColor];
    }
    cell.aliveStatusLabel.string = gameResult.aliveStatus;
    
    cell.killEnemyLabel.string = @(gameResult.killEnemyCount).stringValue;
    cell.expLabel.string = @(gameResult.gotExpPoint).stringValue;
    if (gameResult.addExpPoint > 0) {
        cell.addExpLabel.string = [NSString stringWithFormat:@"(+%tu)", gameResult.addExpPoint];
    } else {
        cell.addExpLabel.string = @"";
    }
    
    return cell;
}

@end
