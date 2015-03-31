//
//  BGEquipment.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGEquipSprite.h"
#import "BGHandCard.h"
#import "BGCardButton.h"

@interface BGEquipment : CCSprite {
    CCNode *_weaponNode, *_armorNode;
    BGCardButton *_weaponButton, *_armorButton;
    CCSprite *_weaponGlow, *_armorGlow;
}

@property (nonatomic, strong) NSMutableArray *equippedCardIds;
@property (nonatomic, strong) NSMutableArray *equippedCards;
@property (nonatomic, strong) NSMutableArray *selectedCards;    // 选中要使用/弃的装备
@property (nonatomic, strong, readonly) BGHandCard *weapon;
@property (nonatomic, strong, readonly) BGHandCard *armor;

- (void)updateEquipmentWithCardIds:(NSArray *)cardIds;
- (void)addEquipmentWithCardIds:(NSArray *)cardIds;
- (void)removeEquipmentWithCardIds:(NSArray *)cardIds;
- (void)removeSelectedEquipments;
- (void)cancelFirstSelectedEquipment;

- (void)enableEquipmentWithCardIds:(NSArray *)cardIds;
- (void)enableAllEquipments;
- (void)disableAllEquipments;
- (void)activateEquipmentWithCardId:(NSInteger)cardId;          // e.g.闪避护符
- (void)deactivateEquipmentWithCardId:(NSInteger)cardId;

- (BGHandCard *)equipmentByCardId:(NSInteger)cardId;
- (void)updateEquipmentBuffer;
- (void)resetSelectedEquipment;

- (void)equipmentSelected:(BGCardButton *)equipButton;

@end
