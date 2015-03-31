//
//  BGChatPopup.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGChatPopup.h"
#import "BGGameScene.h"

#define kPhraseCellIdentifier   @"phraseCell"
#define kHistoryCellIdentifier  @"historyCell"

@implementation BGChatPopup {
    CGPoint _initialPos;
    CCButton *_selectedSegment;
    NSArray *_sortedKeys;
}

static NSMutableArray *chatPhrases;
static NSMutableDictionary *chatHistories;

- (id)init
{
    if (self = [super init]) {
        if (!chatPhrases) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistChatPhrase ofType:kFileTypePLIST];
            chatPhrases = [NSMutableArray arrayWithContentsOfFile:path];
        }
    }
    return self;
}

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    _initialPos = _position = ccp(-self.bgSize.width, self.position.y);
    
#if __CC_PLATFORM_IOS
    _tableView = [[BGTableView alloc] initWithFrame:CGRectZero];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    [self setUITableViewFrame];
    [[CCDirector sharedDirector].view addSubview:_tableView];
#endif
    
    _messageText.platformTextField.returnKeyType = BGReturnKeySend;
    _messageText.delegate = self;
    
    _selectedSegment = _buttonBox.children.firstObject;
    _selectedSegment.selected = YES;
    _selectedSegment.enabled = NO;
}

- (BOOL)isPhraseSegment
{
    return ([_selectedSegment.name isEqual:@"phrase"]);
}

#if __CC_PLATFORM_IOS
- (void)setUITableViewFrame
{
    CGFloat width = self.bgSize.width;
    CGFloat height = self.bgSize.height;
    CGPoint glPos = ccp(self.position.x, height*0.75f);
    CGPoint origin = [[CCDirector sharedDirector] convertToUI:glPos];
    CGSize size = CGSizeMake(width*SCALE_MULTIPLIER*0.95f, height*SCALE_MULTIPLIER*0.75f);
    
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

- (void)show
{
    self.delegate = self.runningSceneNode;
    [self.runningSceneNode addChild:self z:1000];
    
    [self runEaseMoveWithDuration:DURATION_POPUP_MOVE position:CGPointZero block:nil];
}

- (void)dismiss
{
    [self.delegate didDismissPopup:self];
    
    [self runEaseMoveWithDuration:DURATION_POPUP_MOVE position:_initialPos block:^{
        [self removeFromParent];
    }];
}

- (void)removeFromParent
{
    [super removeFromParent];
    [_tableView removeFromSuperview];
}
#endif

#pragma mark - Chat history
+ (void)initializeHistory
{
//  Need initial when room creation
    if (!chatHistories) chatHistories = [NSMutableDictionary dictionary];
}

+ (void)addHistory:(NSString *)message byUser:(NSString *)nickName
{
    NSString *key = [NSString stringWithFormat:@"%@%@%@:", [NSDate date], CONNECTOR_SIGN, nickName];
    chatHistories[key] = message;
}

+ (void)clearHistory
{
    [chatHistories removeAllObjects];
}

#pragma mark - Button selector
- (void)segmentSelected:(CCButton *)sender
{
    _selectedSegment = sender;
    sender.enabled = NO;
    for (CCButton *button in _buttonBox.children) {
        if (![button.name isEqual:sender.name]) {
            button.selected = NO;
            button.enabled = YES;
        }
    }
    
    [self reloadTableViewData];
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidBeginEditing:(CCPlatformTextField *)platformTextField
{
    self.isKeyboardAppeared = YES;
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    if (platformTextField.string.length > 0) {
        [self sendPublicMessage:platformTextField.string];
    }
}

- (void)sendPublicMessage:(NSString *)message
{
    [[BGClient sharedClient] sendPublicMessage:message];
    [self dismiss];
}

- (void)reloadTableViewData
{
#if __CC_PLATFORM_IOS
    if (![self isPhraseSegment]) {
        _sortedKeys = [chatHistories.allKeys sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
            return [obj1 compare:obj2];
        }];
        _tableView.contentOffset = CGPointMake(0.0f, CGFLOAT_MAX);  // Make tableview scroll to bottom
    }
    [_tableView reloadData];
#endif
}

#if __CC_PLATFORM_IOS
#pragma mark - TableView datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return ([self isPhraseSegment]) ? chatPhrases.count : chatHistories.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *identifier = [self isPhraseSegment] ? kPhraseCellIdentifier : kHistoryCellIdentifier;
    UITableViewCellStyle style = [self isPhraseSegment] ? UITableViewCellStyleDefault : UITableViewCellStyleValue2;
    
    BGTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[BGTableViewCell alloc] initWithStyle:style reuseIdentifier:identifier];
        cell.selectionStyle = UITableViewCellSelectionStyleDefault;
        cell.backgroundColor = [UIColor clearColor];
        UIView *bgColorView = [[UIView alloc] init];
        bgColorView.backgroundColor = [UIColor clearColor];
        cell.selectedBackgroundView = bgColorView;
    }
    
    if ([self isPhraseSegment]) {
        cell.textLabel.numberOfLines = 0;   // Multiple lines
        cell.textLabel.font = [UIFont systemFontOfSize:[self fontSize]];
        cell.textLabel.textColor = DETAIL_TEXT_COLOR.UIColor;
        cell.textLabel.highlightedTextColor = [UIColor whiteColor];
        cell.textLabel.text = chatPhrases[indexPath.row];
        tableView.allowsSelection = YES;
    } else {
        cell.textLabel.numberOfLines = 1;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.textLabel.font = [UIFont systemFontOfSize:[self fontSize]];
        cell.textLabel.textColor = TEXT_COLOR.UIColor;
        NSString *key = _sortedKeys[indexPath.row];
        NSRange range = [key rangeOfString:CONNECTOR_SIGN];
        cell.textLabel.text = [key substringFromIndex:range.location+1];
        
        cell.detailTextLabel.numberOfLines = 0;     // Multiple lines
        cell.detailTextLabel.font = [UIFont systemFontOfSize:[self fontSize]];
        cell.detailTextLabel.textColor = DETAIL_TEXT_COLOR.UIColor;
        cell.detailTextLabel.text = chatHistories[key];
        tableView.allowsSelection = NO;
    }
    
    return cell;
}

- (CGFloat)fontSize
{
    return (IS_PHONE) ? 10 : 20;
}

#pragma mark - TableView delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *text = [self isPhraseSegment] ? chatPhrases[indexPath.row] : chatHistories[_sortedKeys[indexPath.row]];
    CGFloat constrainedWidth = [self isPhraseSegment] ? tableView.bounds.size.width*0.9f : tableView.bounds.size.width*DETAIL_LABEL_PERCENTAGE;
    
    CGSize textSize = [text sizeWithFont:[UIFont systemFontOfSize:[self fontSize]]
                       constrainedToSize:CGSizeMake(constrainedWidth, FLT_MAX)
                           lineBreakMode:NSLineBreakByTruncatingTail];
    NSUInteger rowCount = ceil(textSize.height/[self rowHeight]);
    
    return [self rowHeight] * rowCount;
}

- (CGFloat)rowHeight
{
    return (IS_PHONE) ? 20 : 40;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([self isPhraseSegment]) [self sendPublicMessage:chatPhrases[indexPath.row]];
}

#endif

@end
