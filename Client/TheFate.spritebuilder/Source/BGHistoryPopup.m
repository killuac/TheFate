//
//  BGHistoryPopup.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHistoryPopup.h"
#import "BGGameScene.h"

#define kCellIdentifier     @"historyCell"

@implementation BGHistoryPopup {
    CGPoint _initialPos;
    NSDictionary *_gameHistories;
    NSArray *_sortedKeys;
}

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    _initialPos = _position = ccp(SCREEN_WIDTH, self.position.y);
    
#if __CC_PLATFORM_IOS
    _tableView = [[BGTableView alloc] initWithFrame:CGRectZero];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.allowsSelection = NO;
    [self setUITableViewFrame];
    [self loadHistoryData];
    _tableView.contentOffset = CGPointMake(0.0f, CGFLOAT_MAX);  // Make tableview scroll to bottom
    [[CCDirector sharedDirector].view addSubview:_tableView];
#endif
}

- (void)loadHistoryData
{
    _gameHistories = [self.runningSceneNode allGameHistories];
    _sortedKeys = [_gameHistories.allKeys sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        return [obj1 compare:obj2];
    }];
}

- (void)reloadHistoryData
{
    [self loadHistoryData];
#if __CC_PLATFORM_IOS
    [_tableView reloadData];
#endif
}

- (void)show
{
    self.delegate = self.runningSceneNode;
    [self.runningSceneNode addChild:self z:1000];
}

- (void)dismiss
{
    [self dismissWithDuration:DURATION_SWIPE_MINIMUM];
}

- (void)showWithDuration:(NSTimeInterval)duration
{
    CGPoint targetPos = CGPointMake(SCREEN_WIDTH-self.bgSize.width, _background.position.y);
    [self runEaseMoveWithDuration:SWIPE_DURATION(duration) position:targetPos block:nil];
}

- (void)dismissWithDuration:(NSTimeInterval)duration
{
    [self.delegate didDismissPopup:self];
    
    [self runEaseMoveWithDuration:SWIPE_DURATION(duration) position:_initialPos block:^{
        [self removeFromParent];
    }];
}

#if __CC_PLATFORM_IOS
- (void)setUITableViewFrame
{
    CGFloat width = self.bgSize.width;
    CGFloat height = self.bgSize.height;
    CGPoint glPos = ccp(self.position.x+width*0.056f, height*0.9f);
    CGPoint origin = [[CCDirector sharedDirector] convertToUI:glPos];
    CGSize size = CGSizeMake(width*SCALE_MULTIPLIER*0.935f, height*SCALE_MULTIPLIER*0.9f);
    
    if (IS_PHONE) {     // For table view header
        origin = ccp(origin.x*1.002, origin.y);
        size = CGSizeMake(size.width*0.99, size.height);
    }
    
    CGRect frame = _tableView.frame;
    frame.origin = origin;
    frame.size = size;
    _tableView.frame = frame;
}

- (void)setPosition:(CGPoint)position
{
    CGPoint offset = ccpSub(position, self.position);
    [super setPosition:position];
    
    _tableView.center = ccpAdd(_tableView.center, ccpMult(offset, SCALE_MULTIPLIER));
}

- (void)removeFromParent
{
    [super removeFromParent];
    [_tableView removeFromSuperview];
}

#pragma mark - TableView datasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return _sortedKeys.count;
}

//- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
//{
//    NSString *key = _sortedKeys[section];
//    NSRange range = [key rangeOfString:CONNECTOR_SIGN];
//    return [key substringFromIndex:range.location+1];
//}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSArray *playerOneTurnHistory = _gameHistories[_sortedKeys[section]];
    return playerOneTurnHistory.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    BGTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    if (!cell) {
        cell = [[BGTableViewCell alloc] initWithStyle:UITableViewCellStyleValue2 reuseIdentifier:kCellIdentifier];
        cell.backgroundColor = [UIColor clearColor];
    }
    
    BGHistory *history = [self getOneHistoryByIndexPath:indexPath];
    
    cell.textLabel.text = history.heroName;
    cell.textLabel.font = [UIFont systemFontOfSize:[self fontSize]];
    cell.textLabel.textColor = TEXT_COLOR.UIColor;
    
    cell.detailTextLabel.numberOfLines = 0;     // Multiple lines
    cell.detailTextLabel.font = [UIFont systemFontOfSize:[self fontSize]];
    cell.detailTextLabel.textColor = DETAIL_TEXT_COLOR.UIColor;
    cell.detailTextLabel.text = history.text;
    
    return cell;
}

- (CGFloat)fontSize
{
    return (IS_PHONE) ? 10 : 20;
}

- (BGHistory *)getOneHistoryByIndexPath:(NSIndexPath *)indexPath
{
    NSArray *oneTurnHistory = _gameHistories[_sortedKeys[indexPath.section]];
    return oneTurnHistory[indexPath.row];
}

#pragma mark - TableView delegate
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [self headerHeight];
}

- (CGFloat)headerHeight
{
    return (IS_PHONE) ? 15 : 30;
}

- (CGFloat)headerFontSize
{
    return (IS_PHONE) ? 14 : 28;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UITableViewHeaderFooterView *hv = [tableView headerViewForSection:section];
    UIView *headerView = [[UIView alloc] initWithFrame:hv.frame];
    headerView.backgroundColor = [UIColor blackColor];
    
    CGFloat xPos = tableView.bounds.size.width * 0.01f;
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(xPos, 0, tableView.bounds.size.width, [self headerHeight])];
    label.font = [UIFont fontWithName:FONT_BAOLI size:[self headerFontSize]];
    label.textColor = HISTORY_TEXT_COLOR.UIColor;
    label.backgroundColor = [UIColor clearColor];
    [headerView addSubview:label];
    
    NSString *key = _sortedKeys[section];
    NSRange range = [key rangeOfString:CONNECTOR_SIGN];
    label.text = [key substringFromIndex:range.location+1];
    
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    BGHistory *history = [self getOneHistoryByIndexPath:indexPath];
    CGSize textSize = [history.text sizeWithFont:[UIFont systemFontOfSize:[self fontSize]]
                               constrainedToSize:CGSizeMake(tableView.bounds.size.width*DETAIL_LABEL_PERCENTAGE, FLT_MAX)
                                   lineBreakMode:NSLineBreakByTruncatingTail];
    NSUInteger rowCount = ceil(textSize.height/[self rowHeight]);
    return [self rowHeight] * rowCount;
}

- (CGFloat)rowHeight
{
    return (IS_PHONE) ? 15 : 30;
}
#endif

@end
