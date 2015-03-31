//
//  BGCharacter.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCharacter.h"
#import "BGGameScene.h"

@interface BGCharacter ()

@property (nonatomic, weak) BGGameScene *game;
@property (nonatomic, weak) BGPlayer *player;

@end

@implementation BGCharacter

- (void)didLoadFromCCB
{
    _heroAvatar.togglesSelectedState = YES;
    
    [_selectionHalo removeFromParent];
    _heroAvatar.selectionHalo = _selectionHalo;
    _selectionHalo.positionType = CCPositionTypeNormalized;
    _selectionHalo.position = ccp(0.5f, 0.5f);
    _selectionHalo.scaleX /= _heroAvatar.scaleX;
    _selectionHalo.scaleY /= _heroAvatar.scaleY;
    [_heroAvatar addChild:_selectionHalo];
    
    [self disableHeroAvatar];
}

- (BGGameScene *)game
{
    return self.player.game;
}

- (BGPlayer *)player
{
    return (BGPlayer *)self.parent;
}

- (NSString *)heroName {
    return _heroCard.cardText;
}

- (NSInteger)hpLimit {
    return _heroCard.hpLimit;
}

- (NSUInteger)spLimit {
    return _heroCard.spLimit;
}

- (NSUInteger)handSizeLimit {
    return _heroCard.handSizeLimit;
}

- (BGHeroSkill *)heroSkillBySkillId:(BGHeroSkillId)skillId
{
    for (BGHeroSkill *skill in _heroSkills) {
        if (skill.skillId == skillId) return skill;
    }
    return nil;
}

#pragma mark - Hero rendering
- (void)renderHeroWithHeroId:(NSInteger)heroId
{
    _heroCard = [BGHeroCard cardWithCardId:heroId];
    _healthPoint = _heroCard.hpLimit;
    _skillPoint = 0;
    _heroSkills = [BGHeroSkill heroSkillsBySkillIds:_heroCard.skillIds ofPlayer:self.player];
    
    [self renderHeroAvatarAndName];
    [self renderHealthPoint];
    [self renderSkillPoint];
}

- (void)renderHeroAvatarAndName
{
    _heroAvatar.name = @(_heroCard.cardId).stringValue;
    CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:_heroCard.avatarImageName];
    [_heroAvatar setBackgroundSpriteFrame:spriteFrame forState:CCControlStateNormal];
    
    _heroNameLabel.fntFile = _heroCard.avatarFontName;
    _heroNameLabel.string = _heroCard.verticalText;
    _heroNameLabel.scale /= FONT_SCALE_FACTOR;
}

/*
 * Render hero health point
 */
- (void)renderHealthPoint
{
    [_hpLayoutBox removeAllChildren];
    if (self.hpLimit <= 3) {
        _hpLayoutBox.spacing = 10.0f;
    } else if (self.hpLimit == 4) {
        _hpLayoutBox.spacing = 5.0f;
    } else {
        _hpLayoutBox.spacing = 3.0f;
    }
    
    NSString *hpImageName;
    if (1 == _healthPoint) {
        hpImageName = kImageHpRed;
    } else if (2 == _healthPoint) {
        hpImageName = kImageHpYellow;
    } else {
        hpImageName = kImageHpGreen;
    }
    
    for (NSInteger i = 0; i < self.hpLimit; i++) {
        if ((NSInteger)i >= _healthPoint) hpImageName = kImageHpEmpty;
        
        CCSprite *hpSprite = [CCSprite spriteWithImageNamed:hpImageName];
        [_hpLayoutBox addChild:hpSprite];
    }
}

/*
 * Render hero skill point
 */
- (void)renderSkillPoint
{
    [_spLayoutBox removeAllChildren];
    
    NSString *spImageName = kImageSp;
    for (NSUInteger i = 0; i < self.spLimit; i++) {
        if (i >= _skillPoint) spImageName = kImageSpEmpty;
        
        CCSprite *spSprite = [CCSprite spriteWithImageNamed:spImageName];
        [_spLayoutBox addChild:spSprite];
    }
}

#pragma mark - Enablement and color
- (void)enableHeroAvatar
{
    _heroAvatar.enabled = YES;
}

- (void)disableHeroAvatar
{
    _heroAvatar.enabled = _heroAvatar.selected = NO;
}

- (void)unselectHeroAvatar
{
    _heroAvatar.selected = NO;
}

- (void)enableHeroSkillWithSkillIds:(NSArray *)skillIds
{
    for (NSNumber *number in skillIds) {
        if ([self isSkillButtonAddedForSkillId:number.integerValue]) continue;
        
        BGHeroSkill *skill = [self heroSkillBySkillId:number.integerValue];
        CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:kImageSkill];
        CCSpriteFrame *selSpriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:kImageSkillSelected];
        CCButton *skillButton = [CCButton buttonWithTitle:skill.skillText spriteFrame:spriteFrame];
        skillButton.label.fontName = FONT_BAOLI;
        skillButton.label.color = SKILL_BUTTON_TEXT_COLOR;
        skillButton.name = @(skill.skillId).stringValue;
        skillButton.togglesSelectedState = YES;
        [skillButton setBackgroundOpacity:1.0f forState:CCControlStateHighlighted];
        [skillButton setBackgroundSpriteFrame:selSpriteFrame forState:CCControlStateSelected];
        [skillButton setTarget:self selector:@selector(heroSkillTouched:)];
        [_skillBox addChild:skillButton];
    }
    
    if (skillIds.count > 1) {
        _skillBox.scaleX = 0.65f;
        _skillBox.spacing = -12;
    } else {
        _skillBox.scale = 1.0f;
        _skillBox.spacing = 0;
    }
}

- (BOOL)isSkillButtonAddedForSkillId:(NSInteger)skillId
{
    for (CCNode *skillButton in _skillBox.children) {
        if (skillButton.name.integerValue == skillId) return YES;
    }
    return NO;
}

- (void)disableHeroAvatarAndRemoveSkills
{
    _heroAvatar.enabled = _heroAvatar.selected = NO;
    _skillBox.visible = YES;
    [_skillBox removeAllChildren];
}

- (void)setHeroSkillsVisible:(BOOL)isVisible
{
    _skillBox.visible = isVisible;
    
    for (CCButton *skillButton in _skillBox.children) {
        skillButton.selected = NO;
    }
}

#pragma mark - Hp/Sp updating
- (void)updateHealthPointWithCount:(NSInteger)count
{
    if (count == _healthPoint) return;
    
    BGAnimationType type = (count < _healthPoint) ? kAnimationTypeDamaged : kAnimationTypeRestoreHp;
    [self runWithAnimationType:type ofPlayer:self.player atPosition:_heroAvatar.positionInPoints];
    
//  Re-render health point
    _healthPoint = count;
    if (_healthPoint > self.hpLimit) _healthPoint = self.hpLimit;
    [self renderHealthPoint];
}

- (void)updateSkillPointWithCount:(NSUInteger)count
{
    if (count == _skillPoint) return;
    
    if (self.player.hpChanged == 0 && self.player.spChanged > 0 && count > 0) {
        [[BGAudioManager sharedAudioManager] playSPObtain];
    }
    
    _skillPoint = count;
    if (_skillPoint > self.spLimit) _skillPoint = self.spLimit;
    [self renderSkillPoint];
}

#pragma mark - Hero avatar selector
- (void)heroAvatarTouched:(BGHeroButton *)heroButton
{
    if (heroButton.isLongPressed) return;
    
    [self selectTargetPlayer];
    [[BGAudioManager sharedAudioManager] playTargetClick];
}

/*
 * Select other player as target player
 * First touching is selected, second is removed.
 */
- (void)selectTargetPlayer
{
    BGPlayer *actPlayer = self.game.selfPlayer;
    CCButton *okayButton = (CCButton *)[actPlayer.playingButton getChildByName:kButtonNameOkay recursively:NO];
    NSMutableArray *tarPlayerNames = actPlayer.targetPlayerNames;
    NSAssert(tarPlayerNames, @"targetPlayerNames Nil in %@", NSStringFromSelector(_cmd));
    
    if ([tarPlayerNames containsObject:self.player.playerName]) {
        [actPlayer checkTargetPlayerEnablement];
        [tarPlayerNames removeObject:self.player.playerName];
    }
    else {
        [tarPlayerNames addObject:self.player.playerName];
//      If great than selectable target count, need remove the first selected target player.
        if (tarPlayerNames.count > actPlayer.maxTargetCount) {
            [[actPlayer.targetPlayers.firstObject character] unselectHeroAvatar];
            [tarPlayerNames removeObjectAtIndex:0];
        }
    }
    
    [actPlayer.heroSkill checkHandCardEnablement];
    if (tarPlayerNames.count > 0) [self checkNextTargetPlayerEnablement];
    
    okayButton.enabled = actPlayer.isOkayEnabled;
}

/*
 * If the selected skill/card need multiple target players, need check next enabled player.
 */
- (void)checkNextTargetPlayerEnablement
{
    BGPlayer *actPlayer = self.game.selfPlayer;
    BGHandCard *card = actPlayer.handArea.selectedCard;
    id<BGChecker> checker = (actPlayer.heroSkill) ? actPlayer.heroSkill : card;
    if (actPlayer.requiredTargetCount < 2) return;
    
    for (BGPlayer *player in self.game.alivePlayers) {
        if ([actPlayer.targetPlayers containsObject:player]) continue;  // Need skip for already selected player
        
        if ([checker checkNextPlayerEnablement:player]) {
            [player enablePlayerArea];
        } else {
            [player disablePlayerAreaWithDarkColor];
        }
    }
}

- (void)checkTargetPlayerEnablement
{
    if (GameStateDeathResolving == self.game.state) [self.game enablePlayerAreaForOtherPlayers];
    
    if (GameStatePlaying != self.game.state && GameStateTargetChoosing != self.game.state) return;
    
    for (BGPlayer *player in self.game.alivePlayers) {
        if ([self.player.heroSkill checkPlayerEnablement:player]) {
            [player enablePlayerArea];
        } else {
            [player disablePlayerAreaWithDarkColor];
        }
    }
}

#pragma mark - Hero skill selector
- (void)heroSkillTouched:(CCButton *)sender
{
    [self selectHeroSkill:sender];
    [[BGAudioManager sharedAudioManager] playHeroSkillClick];
}

- (void)selectHeroSkill:(CCButton *)sender
{
    for (CCButton *skillButton in _skillBox.children) {
        if (![skillButton.name isEqualToString:sender.name]) {
            skillButton.selected = NO;
        }
    }
    
    BGHeroSkill *heroSkill = [self heroSkillBySkillId:sender.name.integerValue];
    heroSkill.isSelected = !heroSkill.isSelected;
    
    if (heroSkill.isTargetable) [self.player clearSelectedTargetPlayers];
    [self.player.playingButton setOkayEnabled:NO];
    [self.player.handArea resetSelectedHandCard];
    [self.player.handArea disableAllHandCardsWithDarkColor];
    [self.player.equipment resetSelectedEquipment];
    [self.player.equipment disableAllEquipments];
    
    if (sender.selected) {
        self.player.heroSkillId = sender.name.integerValue;
        self.player.requiredTargetCount = self.player.isTurnOwner ? heroSkill.targetCount : 0;
        self.player.maxTargetCount = heroSkill.maxTargetCount;
        [self.player addTextPromptForSelectedHeroSkill];
        [self.player useHeroSkill];
        if (heroSkill.isTargetable) [self checkTargetPlayerEnablement];
    }
    else {
        [self.player cancelHeroSkill];
        [self.player addTextPrompt];
        [self.game disablePlayerAreaForAllPlayers];
        if (!self.game.isWaitingDispel) [self.player resetValueAfterResolved];
    }
}

- (void)resetHeroSkill
{
    for (BGHeroSkill *skill in _heroSkills) {
        skill.isSelected = NO;
        skill.isSoundPlayed = NO;
    }
}

@end
