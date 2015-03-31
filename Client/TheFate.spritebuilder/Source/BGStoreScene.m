//
//  BGStoreScene.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGStoreScene.h"
#import "BGHeroCard.h"
#import "BGHeroSprite.h"
#import "BGRoomScene.h"
#if __CC_PLATFORM_IOS
#import "BGStoreKitHelper.h"
#endif

@implementation BGStoreScene {
    CCButton *_selectedSegment;
    id _boughtMerchandise;
    
    CGPoint _initialScrollNodePos;
    CGPoint _beganScrollNodePos;
    NSTimeInterval _beganTimestamp;
}

static NSDictionary *merchandises;

- (id)init
{
    if (self = [super init]) {
        if (!merchandises) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistMerchandise ofType:kFileTypePLIST];
            merchandises = [NSDictionary dictionaryWithContentsOfFile:path];
        }
        
        
    }
    return self;
}

- (void)didLoadFromCCB
{
    self.userInteractionEnabled = YES;
//    _scrollView = [[BGScrollView alloc] initWithFrame:CGRectZero];  // View container for all merchandise's description(UITextView)
    _initialScrollNodePos = _scrollNode.position;
    
    [self updateGoldCoin:NO];
}

- (void)updateGoldCoin:(BOOL)isRecharge
{
    if (isRecharge) {
        [[BGAudioManager sharedAudioManager] playRecharge];
    } else {
        _goldCoinLabel.string = @([BGClient sharedClient].esUser.goldCoin).stringValue;
    }
}

- (void)update:(CCTime)delta
{
    NSInteger coinCount = [BGClient sharedClient].esUser.goldCoin;
    
    if (_goldCoinLabel.string.integerValue < coinCount) {
        NSInteger increment = coinCount / 60;
        _goldCoinLabel.string = @(_goldCoinLabel.string.integerValue+increment).stringValue;
    } else {
        _goldCoinLabel.string = @(coinCount).stringValue;
    }
}

- (void)loadData
{
    if (_isPickHero) {
        _pickHeroBox.visible = YES;
        _pickHeroTitle.scale /= FONT_SCALE_FACTOR;
        [_buttonBox.children.firstObject removeFromParent];
        [self loadPickingHeroCardsByAttribute:HeroAttributeStrength];
    } else {
        [_buttonBox.children.lastObject removeFromParent];
        [self loadSaleHeroCards];
    }
    
    _selectedSegment = [_buttonBox.children.firstObject children].firstObject;
    _selectedSegment.selected = YES;
    _selectedSegment.userInteractionEnabled = NO;
    _selectedSegment.label.fontColor = SELECTED_FONT_COLOR;
}

- (void)loadPickingHeroCardsByAttribute:(BGHeroAttribute)attribute
{
    NSMutableArray *heroIds = [NSMutableArray array];
    for (NSNumber *number in _availableHeroIds) {
        BGHeroCard *heroCard = [BGHeroCard cardWithCardId:number.integerValue];
        if (heroCard.attribute == attribute) [heroIds addObject:number];
    }
    NSArray *heroCards = [BGHeroCard heroCardsWithHeroIds:heroIds];
    
    for (BGHeroCard *card in heroCards) {
        BGHeroSprite *heroSprite = (BGHeroSprite *)[CCBReader load:kCcbiHeroSprite owner:self];
        heroSprite.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:card.imageName];
        heroSprite.name = @(card.cardId).stringValue;
        heroSprite.nameLabel.string = card.cardText;
        [_scrollNode addChild:heroSprite];
    }
}

- (void)loadSaleHeroCards
{
//    [[BGHeroCard saleHeroCards] enumerateObjectsUsingBlock:^(BGHeroCard *card, NSUInteger idx, BOOL *stop) {
//        BGMerchandise *merchandise = (BGMerchandise *)[CCBReader load:kCcbiMerchandise owner:self];
//        merchandise.mechandiseId = @(card.cardId).stringValue;
//        merchandise.nameLabel.string = card.cardText;
//        merchandise.imageName = card.cardName;
//        merchandise.merchandiseDesc = card.cardDesc;
//        merchandise.priceLabel.string = @(card.price).stringValue;
//        merchandise.buyButton.name = @(idx).stringValue;
//        [[merchandise getChildByName:@"currency" recursively:YES] removeFromParent];
//        [_scrollNode addChild:merchandise];
//        
//        merchandise.textView.textAlignment = UITextAlignmentLeft;
//        [_scrollView addSubview:merchandise.textView];
//    }];
//    _scrollView.contentSize = _scrollNode.contentSize;
    
    [[BGHeroCard saleHeroCards] enumerateObjectsUsingBlock:^(BGHeroCard *card, NSUInteger idx, BOOL *stop) {
        BGHeroSprite *heroSprite = (BGHeroSprite *)[CCBReader load:kCcbiHeroSprite owner:self];
        heroSprite.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:card.imageName];
        heroSprite.buyButton.visible = heroSprite.priceBox.visible = YES;
        heroSprite.name = @(card.cardId).stringValue;
        heroSprite.nameLabel.string = card.cardText;
        heroSprite.priceLabel.string = @(card.price).stringValue;
        heroSprite.buyButton.name = @(idx).stringValue;
        [_scrollNode addChild:heroSprite];
        
        if ([[BGClient sharedClient].esUser.ownHeroIds containsObject:@(card.cardId)]) {
            heroSprite.buyButton.enabled = NO;
        }
    }];
}

- (void)loadVIPData
{
    NSArray *vips = merchandises[@"VIP"];
    [_vipTypeIds enumerateObjectsUsingBlock:^(NSNumber *number, NSUInteger idx, BOOL *stop) {
        NSDictionary *vip = vips[number.integerValue];
        
        BGMerchandise *merchandise = (BGMerchandise *)[CCBReader load:kCcbiMerchandise owner:self];
        merchandise.mechandiseId = @(number.integerValue).stringValue;
        merchandise.nameLabel.string = vip[kTitle];
        merchandise.imageName = vip[kName];
        merchandise.merchandiseDesc = [self descriptionForVIP:vip];
        merchandise.priceLabel.string = @([vip[kPrice] integerValue]).stringValue;
        merchandise.buyButton.name = @(idx).stringValue;
        [[merchandise getChildByName:@"currency" recursively:YES] removeFromParent];
        [_scrollNode addChild:merchandise];
    }];
}

- (NSString *)descriptionForVIP:(NSDictionary *)vip
{
    NSString *conHeroName = @"";
    for (NSNumber *number in vip[kFreeUsedHeros]) {
        BGHeroCard *card = [BGHeroCard cardWithCardId:number.integerValue];
        conHeroName = [NSString stringWithFormat:@"%@%@%@", conHeroName, DELIMITER_SIGN, card.cardText];
    }
    
    return [BGUtil textWith:vip[kDescription] parameter:[conHeroName substringFromIndex:1]];
}

- (void)loadGoldCoinData
{
    NSDictionary *goldCoins = merchandises[@"goldCoin"];
    [_goldCoinIds enumerateObjectsUsingBlock:^(NSString *productId, NSUInteger idx, BOOL *stop) {
        NSDictionary *goldCoin = goldCoins[productId];
        
        BGMerchandise *merchandise = (BGMerchandise *)[CCBReader load:kCcbiMerchandise owner:self];
        merchandise.mechandiseId = productId;
        merchandise.nameLabel.string = goldCoin[kTitle];
        merchandise.imageName = goldCoin[kName];
        merchandise.merchandiseDesc = goldCoin[kDescription];
        merchandise.priceLabel.string = @([goldCoin[kPrice] integerValue]).stringValue;
        merchandise.buyButton.name = @(idx).stringValue;
        [[merchandise getChildByName:@"coin" recursively:YES] removeFromParent];
        [_scrollNode addChild:merchandise];
    }];
}

#pragma mark - Button selector
- (void)segmentSelected:(CCButton *)sender
{
    _scrollNode.position = _initialScrollNodePos;
    [_scrollNode removeAllChildren];
//    [_scrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    [self removeHeroInformation];
    
    sender.userInteractionEnabled = NO;
    sender.label.fontColor = SELECTED_FONT_COLOR;
    _selectedSegment = sender;
    for (CCButton *button in [_buttonBox.children.firstObject children]) {
        if (![button.name isEqual:sender.name]) {
            button.selected = NO;
            button.userInteractionEnabled = YES;
            button.label.fontColor = NORMAL_FONT_COLOR;
        }
    }
    
    if ([sender.name isEqualToString:kSegmentHero]) {
        [self loadSaleHeroCards];
    }
    else if ([sender.name isEqualToString:kSegmentMember]) {
        [self loadVIPData];
    }
    else if ([sender.name isEqualToString:kSegmentGoldCoin]) {
        [self loadGoldCoinData];
    }
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

- (void)heroSegmentSelected:(CCButton *)sender
{
    _scrollNode.position = _initialScrollNodePos;
    [_scrollNode removeAllChildren];
    [self removeHeroInformation];
    
    sender.userInteractionEnabled = NO;
    sender.label.fontColor = SELECTED_FONT_COLOR;
    _selectedSegment = sender;
    for (CCButton *button in [_buttonBox.children.firstObject children]) {
        if (![button.name isEqual:sender.name]) {
            button.selected = NO;
            button.userInteractionEnabled = YES;
            button.label.fontColor = NORMAL_FONT_COLOR;
        }
    }
    
    [self loadPickingHeroCardsByAttribute:sender.name.integerValue];
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

- (void)buy:(CCButton *)sender
{
    [[BGAudioManager sharedAudioManager] playGoldCoin];
    _boughtMerchandise = _scrollNode.children[sender.name.integerValue];
    
    if ([_selectedSegment.name isEqualToString:kSegmentGoldCoin]) {
        _boughtProdcutId = [_boughtMerchandise mechandiseId];
        #if __CC_PLATFORM_IOS
        [[BGStoreKitHelper sharedStoreKitHelper] requestProductDataWithProductId:_boughtProdcutId];
        #endif
    } else {
        if ([BGClient sharedClient].esUser.goldCoin < [_boughtMerchandise priceLabel].string.integerValue) {
            [self showCoinNotEnoughTip]; return;
        }
        #if __CC_PLATFORM_IOS
        NSString *message = [BGUtil textWith:ALERT_PURCHASE parameter:[_boughtMerchandise nameLabel].string];
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil
                                                            message:message
                                                           delegate:self
                                                  cancelButtonTitle:ALERT_BUTTON_CANCEL
                                                  otherButtonTitles:ALERT_BUTTON_OKAY, nil];
        [alertView show];
        #endif
    }
}

- (void)back:(CCButton *)sender
{
    sender.enabled = NO;
    
    if (_isPickHero) {
        [self backToRoomScene];
    } else {
        [BGTransitionManager transitSceneToDown:kCcbiZoneScene];
    }
    
    [[BGAudioManager sharedAudioManager] playBigButtonClick];
}

- (BGRoomScene *)backToRoomScene
{
    NSString *fileName = FILE_FULL_NAME(kCcbiRoomScene, [BGClient sharedClient].esRoom.capacity, kFileTypeCCBI);
    return [BGTransitionManager transitSceneToDown:fileName];
}

#if __CC_PLATFORM_IOS
#pragma mark - UIAlert delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (0 == buttonIndex) return;
    
    if ([_selectedSegment.name isEqual:kSegmentHero]) {
        BGHeroSprite *heroSprite = ((BGHeroSprite *)_boughtMerchandise);
        heroSprite.buyButton.enabled = NO;
        
        _boughtHeroId = heroSprite.name.integerValue;
        [[BGClient sharedClient] sendBuyHeroCardRequest];
    }
    else if ([_selectedSegment.name isEqualToString:kSegmentMember]) {
        _boughtVIPTypeId = [_boughtMerchandise mechandiseId].integerValue;
        [[BGClient sharedClient] sendBuyVIPCardRequest];
    }
}
#endif

- (void)showCoinNotEnoughTip
{
    [BGUtil showInformationWithMessage:INFO_COIN_NOT_ENOUGH];
}

#pragma mark - Touch event
- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
//    [_scrollView stopScrollAnimation];
    
    _beganTimestamp = touch.timestamp;
    _beganScrollNodePos = _scrollNode.position;
}

- (BOOL)isScrollableForTouchedPos:(CGPoint)touchPos
{
    return (_scrollNode.contentSize.width >= SCREEN_WIDTH-_scrollNode.spacing &&
            touchPos.y <= CGRectGetMaxY(_scrollNode.boundingBox));
}

- (void)touchMoved:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
//    [_scrollView stopScrollAnimation];
    
    CGPoint offsetPos = touch.offsetPosition;
    if (_scrollNode.position.x > SCREEN_WIDTH*0.4f ||
        _scrollNode.position.x < -(_scrollNode.contentSize.width-SCREEN_WIDTH*0.6f)) {
        offsetPos.x /= 10;
    }
    
    if ([self isScrollableForTouchedPos:[touch glLocationInTouchedView]]) {
        _scrollNode.position = ccpAdd(_scrollNode.position, ccp(offsetPos.x, 0.0f));
    }
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    if ([self isOnHeroSegment] && touch.tapCount > 0) {
        BGHeroSprite *heroSprite = [self heroNodeByTouch:touch];
        if (heroSprite) {
            [self selectHeroCard:heroSprite];
        } else {
            [self removeHeroInformation];
        }
    }
    
    if ([self isScrollableForTouchedPos:[touch glLocationInTouchedView]]) {
        CGPoint movement = ccpSub(_scrollNode.position, _beganScrollNodePos);
        NSTimeInterval timeInterval = (touch.timestamp-_beganTimestamp);
        double xVelocity = (0 == timeInterval) ? 0.0 : (movement.x/timeInterval)/2;
        xVelocity = (xVelocity > 0) ? MIN(xVelocity, SCREEN_WIDTH) : MAX(xVelocity, -SCREEN_WIDTH);
        
        CGFloat xMovement = _scrollNode.position.x + xVelocity;
        CGFloat offsetX = (_scrollNode.position.x >= _beganScrollNodePos.x) ?
            MIN(xMovement, _scrollNode.spacing) :
            MAX(xMovement, SCREEN_WIDTH-_scrollNode.contentSize.width-_scrollNode.spacing);
        
        [_scrollNode runEaseMoveWithDuration:DURATION_SCROLL position:ccp(offsetX, _scrollNode.position.y) block:nil];
    }
}

- (BOOL)isOnHeroSegment
{
    return ([_selectedSegment.name isEqual:kSegmentHero] || _isPickHero);
}

- (BGHeroSprite *)heroNodeByTouch:(CCTouch *)touch
{
    for (BGHeroSprite *sprite in _scrollNode.children) {
        if ([self isTouched:touch onNode:sprite]) return sprite;
    }
    return nil;
}

- (BOOL)isTouched:(CCTouch *)touch onNode:(CCNode *)node
{
    CGPoint touchPos = [self convertToNodeSpace:[touch glLocationInTouchedView]];
    touchPos = ccp(touchPos.x, touchPos.y-_scrollNode.positionInPoints.y);
    return CGRectContainsPoint(node.boundingBox, touchPos);
}

- (void)selectHeroCard:(BGHeroSprite *)heroSprite
{
    heroSprite.selected = !heroSprite.selected;
    
    if (heroSprite.selected) {
        for (BGHeroSprite *sprite in _scrollNode.children) {
            if (![sprite isEqual:heroSprite]) sprite.selected = NO;
        }
        
        if (!_heroInformation) {
            _heroInformation = (BGHeroInformation *)[CCBReader load:kCcbiHeroInformation];
            [_heroInformation showFromBottomInNode:self];
        }
        
        BGHeroCard *heroCard = [BGHeroCard cardWithCardId:heroSprite.name.integerValue];
        _heroInformation.heroDesc = heroCard.cardDesc;
    } else {
        [self removeHeroInformation];
    }
    
    if (_isPickHero && !heroSprite.selected) {
        NSUInteger price = [merchandises[@"heroSelection"][@"pickOneHero"] integerValue];
        if ([BGClient sharedClient].esUser.goldCoin < price) {
            [self showCoinNotEnoughTip];
        } else {
            self.userInteractionEnabled = NO;
            [self addPopupMaskNode];
            _boughtMerchandise = heroSprite;
            
            NSString *message = [BGUtil textWith:ALERT_PICK_HERO parameters:@[@(price).stringValue, heroSprite.nameLabel.string]];
            [BGUtil showAlertPopupWithMessage:message];
        }
        
        [[BGAudioManager sharedAudioManager] playGoldCoin];
    }
}

#pragma mark - Popup delegate
- (void)popupOkay:(BGPopup *)popup
{
    _pickedHeroId = ((BGHeroSprite *)_boughtMerchandise).name.integerValue;
    [[BGClient sharedClient] sendPickOneHeroRoomRequest];
}

- (void)didDismissPopup:(BGPopup *)popup
{
    self.userInteractionEnabled = YES;
    [self removePopupMaskNode];
    [_effectNode clearEffect];
}

#pragma mark - Transition
- (void)onEnterTransitionDidFinish
{
    [super onEnterTransitionDidFinish];
    
//    CGRect frame = _scrollView.frame;
//    frame.size = CGSizeMake(SCREEN_WIDTH*SCALE_MULTIPLIER, SCREEN_HEIGHT*SCALE_MULTIPLIER);
//    _scrollView.frame = frame;
//    _scrollView.contentOffset = ccp(-_scrollNode.spacing*SCALE_MULTIPLIER, 0.0f);
//    [[CCDirector sharedDirector].view addSubview:_scrollView];
}

- (void)onExitTransitionDidStart
{
    [super onExitTransitionDidStart];
//    [_scrollView removeFromSuperview];
    [self removeHeroInformation];
    
#if __CC_PLATFORM_IOS
    [[BGStoreKitHelper sharedStoreKitHelper] removeTransactionObserver];
#endif
}

- (void)removeHeroInformation
{
    [_heroInformation removeFromParent];
    _heroInformation = nil;
    
    for (id node in _scrollNode.children) {
        if ([node isKindOfClass:[BGHeroSprite class]]) [node setSelected:NO];
    }
}

#pragma mark - Game scene
- (BGGameScene *)showGameScene
{
    NSString *fileName = FILE_FULL_NAME(kCcbiGameScene, [BGClient sharedClient].esRoom.capacity, kFileTypeCCBI);
    return [BGTransitionManager transitSceneToLeft:fileName];
}

@end
