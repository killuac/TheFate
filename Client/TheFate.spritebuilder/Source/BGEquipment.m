//
//  BGEquipment.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGEquipment.h"
#import "BGGameScene.h"

@interface BGEquipment ()

@property (nonatomic, weak) BGGameScene *game;
@property (nonatomic, weak) BGPlayer *player;

@end

@implementation BGEquipment

- (void)didLoadFromCCB
{
    self.equippedCards = [NSMutableArray arrayWithCapacity:COUNT_DEFAULT_OWN_EQUIPMENT];    // 武器和防具
    self.selectedCards = [NSMutableArray array];
}

- (BGGameScene *)game
{
    return self.player.game;
}

- (BGPlayer *)player
{
    return (BGPlayer *)self.parent;
}

- (NSMutableArray *)equippedCardIds
{
    return [BGHandCard playingCardIdsByCards:_equippedCards];
}

- (BGHandCard *)weapon
{
    for (BGHandCard *card in _equippedCards) {
        if (EquipmentTypeWeapon == card.equipmentType)
            return card;
    }
    return nil;
}

- (BGHandCard *)armor
{
    for (BGHandCard *card in _equippedCards) {
        if (EquipmentTypeArmor == card.equipmentType)
            return card;
    }
    return nil;
}

- (void)updateEquipmentBuffer
{
    [_equippedCards removeObjectsInArray:_selectedCards];
    [_selectedCards removeAllObjects];
}

- (void)resetSelectedEquipment
{
    for (BGHandCard *card in _equippedCards) {
        card.isSelected = NO;
        card.isAnimationRan = NO;
    }
    
    _weaponButton.selected = _weaponGlow.visible = NO;
    _armorButton.selected = _armorGlow.visible = NO;
    
    [_selectedCards removeAllObjects];
}

- (BGHandCard *)equipmentByCardId:(NSInteger)cardId
{
    for (BGHandCard *card in _equippedCards) {
        if (card.cardId == cardId) return card;
    }
    return nil;
}

#pragma mark - Equipment updating
/*
 * Update equipments with equipped card or the card id list that received from server
 * Update(Add/Remove) equipment card with hand card while euqipping
 */
- (void)updateEquipmentWithCardIds:(NSArray *)cardIds
{
    NSMutableArray *newCardIds = [cardIds mutableCopy];
    NSMutableArray *equippedCardIds = [BGPlayingCard playingCardIdsByCards:_equippedCards];
    if ([cardIds isEqualToArray:equippedCardIds]) return;
    
//  Equipment id list different between client and server after rejoin game
    if (UpdateReasonRejoin == self.player.updateReason) {
        [self clearEquipmentCards];
        [self addEquipmentWithCardIds:cardIds];
        return;
    }
    
    if (newCardIds.count > equippedCardIds.count) {
        [newCardIds removeObjectsInArray:equippedCardIds];
        [self addEquipmentWithCardIds:newCardIds];
    }
    else if (newCardIds.count < equippedCardIds.count) {
        [equippedCardIds removeObjectsInArray:newCardIds];
        [self removeEquipmentWithCardIds:equippedCardIds];
    }
    else {
        self.player.updateReason = UpdateReasonDefault;
        [self clearEquipmentCards];
        [self addEquipmentWithCardIds:cardIds];
    }
}

- (void)addEquipmentWithCardIds:(NSArray *)cardIds
{
    [[BGAudioManager sharedAudioManager] playCardEquip];
    
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.player];
    for (BGHandCard *card in cards) {
        [self addEquipmentWithCard:card];
    }
}

- (BGCardButton *)getEquipmentButtonByCard:(BGHandCard *)card
{
    return (EquipmentTypeWeapon == card.equipmentType) ? _weaponButton : _armorButton;
}

/*
 * If exist same type equipment(Weapon/Armor), remove the existing one(Replaced).
 * Show the replaced equipment card on the table
 */
- (void)addEquipmentWithCard:(BGHandCard *)card
{
    NSMutableArray *removeCards = [NSMutableArray array];
    
    if (card.isEquippedOne) {   // 圣者遗物(不能装备防具)
        [_weaponButton removeFromParent];
        [_armorButton removeFromParent];
        if (self.weapon) [removeCards addObject:self.weapon];
        if (self.armor) [removeCards addObject:self.armor];
    }
    else {
        if (EquipmentTypeWeapon == card.equipmentType) {
            [_weaponButton removeFromParent];
            if (self.weapon) [removeCards addObject:self.weapon];
        } else {
            [_armorButton removeFromParent];
            if (self.armor) [removeCards addObject:self.armor];
        }
    }
    
//  Remove existing weapon/armor, show it on on the table.
    if (removeCards.count > 0) {
        NSArray *cardButtons = [BGButtonFactory createCardButtonsWithCards:removeCards forTarget:nil];
        for (BGCardButton *cardButton in cardButtons) {
            cardButton.position = ccpAdd(self.player.centerPosition, ccp(self.positionInPoints.x/2, 0.0f));
        }
        [self.game.table showPlayedCardWithCardNodes:cardButtons andClearTable:YES];
    }
    
//  Render the new equipment
    [self renderEquipmentWithCard:card];
    
    [self updateEquipmentBufferWithCard:card];
}

/*
 * Remove equipment since by greeded or disarmed
 */
- (void)removeEquipmentWithCardIds:(NSArray *)cardIds
{
    NSArray *cards = [BGHandCard handCardsByCardIds:cardIds ofPlayer:self.player];
    for (BGHandCard *card in cards) {
        [self removeEquipmentWithCard:card];
    }
    
    [[BGAudioManager sharedAudioManager] playCardEquip];
}

- (void)removeSelectedEquipments
{
    NSMutableArray *cardIds = [BGHandCard handCardIdsByCards:_selectedCards ofPlayer:self.player];
    [self.player.playedCardIds addObjectsFromArray:cardIds];
    
    self.player.updateReason = UpdateReasonTable;
    for (BGHandCard *card in _selectedCards) {
        [self removeEquipmentWithCard:card];
    }
    
    [self.player.equipment updateEquipmentBuffer];
}

// Remove one equipment
- (void)removeEquipmentWithCard:(BGHandCard *)card
{
    [self removeEquipmentFromBufferWithCard:card];
    
    BGCardButton *equipButton = [self getEquipmentButtonByCard:card];
    [equipButton removeFromParent];
    
    BGCardButton *cardButton = [BGButtonFactory createCardButtonWithCard:card forTarget:nil];
    cardButton.position = ccpAdd(self.player.centerPosition, ccp(self.positionInPoints.x, 0.0f));
    
    if (UpdateReasonTable == self.player.updateReason) {
        BOOL isClear = !(self.player.isDead ||
                         ActionChooseCardToDrop == self.player.game.action ||
                         ActionChoseCardToDrop == self.player.game.action);
        [self.game.table showPlayedCardWithCardNodes:@[cardButton] andClearTable:isClear];
    }
    else if (UpdateReasonPlayer == self.player.updateReason) {
        [self.game moveCard:cardButton toPlayer:self.game.turnOwner];
    }
}

- (void)cancelFirstSelectedEquipment
{
    BGCardButton *equipButton = [self getEquipmentButtonByCard:_selectedCards.firstObject];
    if (equipButton) {
        equipButton.selected = NO;
        [self equipmentSelected:equipButton];
    }
}

#pragma mark - Equipment card buffer
/*
 * Update(Add/Replace) card to equipment buffer
 */
- (void)updateEquipmentBufferWithCard:(BGHandCard *)card
{
    if (card.isEquippedOne) {    // 圣者遗物(不能装备防具)
        [_equippedCards removeAllObjects];
        [_equippedCards addObject:card];
        return;
    }
    
    for (id obj in _equippedCards) {
        if ([obj equipmentType] == card.equipmentType) {
            [_equippedCards removeObject:obj];
            break;
        }
    }
    
    [_equippedCards addObject:card];
}

- (void)removeEquipmentFromBufferWithCard:(BGHandCard *)card
{
    for (id obj in _equippedCards) {
        if ([obj cardId] == card.cardId) {
            [_equippedCards removeObject:obj];
            break;
        }
    }
}

- (void)clearEquipmentCards
{
    [_equippedCards removeAllObjects];
    [_weaponButton removeFromParent];
    [_armorButton removeFromParent];
}

#pragma mark - Equipment rendering
/*
 * Render the equipment card after equipped
 */
- (void)renderEquipmentWithCard:(BGHandCard *)card
{
    BGCardButton *equipButton = [self getEquipmentButtonByCard:card];
    BGCardButton *newEquipButton = [BGButtonFactory createEquipmentButtonWithCard:card forTarget:self];
    newEquipButton.enabled = NO;
    newEquipButton.positionType = equipButton.positionType;
    newEquipButton.position = equipButton.position;
    newEquipButton.scaleX = equipButton.scaleX;
    newEquipButton.scaleY = equipButton.scaleY;
    
//  Render equipment name
    CCLabelBMFont *nameLabel = (CCLabelBMFont *)[newEquipButton getChildByName:@"equipName" recursively:NO];
    nameLabel.fntFile = card.avatarFontName;
    nameLabel.scale /= FONT_SCALE_FACTOR;
    
    if (EquipmentTypeWeapon == card.equipmentType) {
        [_weaponNode addChild:newEquipButton z:-1];
        _weaponButton = newEquipButton;
    } else {
        [_armorNode addChild:newEquipButton z:-1];
        _armorButton = newEquipButton;
    }
}

#pragma mark - Equipment selector
- (void)equipmentSelected:(BGCardButton *)equipButton
{
    if (equipButton.isLongPressed) return;
    
    _weaponGlow.visible = _armorGlow.visible = NO;
    [self selectEquipment:equipButton];
    
    [[BGAudioManager sharedAudioManager] playEquipmentClick];
}

- (void)selectEquipment:(BGCardButton *)equipButton
{
    BGHandCard *selCard;
    for (BGHandCard *card in _equippedCards) {
        if (card.cardId == equipButton.name.integerValue) {
            selCard = card; break;
        }
    }
    selCard.isSelected = !selCard.isSelected;
    
    if (ActionChooseCardToDrop == self.game.action &&
        [self.player.droppableEquipIds containsObject:@(selCard.cardId)]) {
        if (selCard.isSelected) {
//          If selected cards count great than maximum, deselect and remove the first selected card.
            [_selectedCards addObject:selCard];
            if (self.player.selectedCardCount > self.player.selectableCardCount) {
                if (self.player.selectedHandCardCount > 0) {
                    [self.player.handArea cancelFirstSelectedCard];
                } else {
                    [_selectedCards removeObjectAtIndex:0];
                }
            }
        } else {
            [_selectedCards removeObject:selCard];
        }
        [self.player.heroSkill checkHandCardEnablement];
        [self.player checkPlayingButtonEnablementWithSelectedCard:selCard];
        return;
    }
    
    if (selCard.isTargetable) [self.player clearSelectedTargetPlayers];
    [self.player.playingButton setOkayEnabled:NO];
    [self.player.handArea resetSelectedHandCard];
    [self.player.handArea disableAllHandCardsWithDarkColor];
    
    if (equipButton.selected) {
        [self.player.character setHeroSkillsVisible:NO];
        
        self.player.equipmentId = equipButton.name.integerValue;
        self.player.requiredTargetCount = self.player.isTurnOwner ? selCard.targetCount : 0;
        self.player.maxTargetCount = self.player.requiredTargetCount;
        [self.player addTextPromptForSelectedEquipment];
        [self.player useEquipment];
        if (selCard.isTargetable) [self.player checkTargetPlayerEnablement];
    }
    else {
        [self.player.character setHeroSkillsVisible:YES];
        
        [self.player cancelEquipment];
        [self.player addTextPrompt];
        [self.game disablePlayerAreaForAllPlayers];
        if (!self.game.isWaitingDispel) [self.player resetValueAfterResolved];
    }
}

- (void)enableEquipmentWithCardIds:(NSArray *)cardIds
{
    _weaponButton.enabled = _weaponGlow.visible = [cardIds containsObject:@(_weaponButton.name.integerValue)];
    _armorButton.enabled = _armorGlow.visible = [cardIds containsObject:@(_armorButton.name.integerValue)];
    
    [self runEquipmentAnimation];
}

- (void)enableAllEquipments
{
    _weaponButton.enabled = YES;
    _armorButton.enabled = YES;
    
    [self runEquipmentAnimation];
}

- (void)disableAllEquipments
{
    _weaponButton.enabled = _weaponButton.selected = _weaponGlow.visible = NO;
    _armorButton.enabled = _armorButton.selected = _armorGlow.visible = NO;
}

- (void)runEquipmentAnimation
{
    if (self.player.isMe && self.weapon && _weaponButton.enabled) {
        _weaponGlow.visible = YES;
        [self.userObject runAnimationsForSequenceNamed:@"WeaponGlow"];
    }
    
    if (self.player.isMe && self.armor && _armorButton.enabled) {
        _armorGlow.visible = YES;
        [self.userObject runAnimationsForSequenceNamed:@"ArmorGlow"];
    }
}

- (void)activateEquipmentWithCardId:(NSInteger)cardId
{
    BGHandCard *equipCard = [self equipmentByCardId:cardId];
    BGCardButton *cardButton = [self getEquipmentButtonByCard:equipCard];
    [cardButton removeFromParent];      // Remove existing
    
    [self renderEquipmentWithCard:equipCard];   // Rerender
}

- (void)deactivateEquipmentWithCardId:(NSInteger)cardId
{
    BGCardButton *cardButton = [self getEquipmentButtonByCard:[self equipmentByCardId:cardId]];
    [cardButton useGrayscaleShaderByRecursively:YES];
    cardButton.rotation = 180;
}

@end
