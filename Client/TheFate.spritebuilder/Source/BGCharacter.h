//
//  BGCharacter.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHeroCard.h"
#import "BGHeroButton.h"

@interface BGCharacter : CCSprite {
    BGHeroButton *_heroAvatar;
    CCLabelBMFont *_heroNameLabel;
    CCLayoutBox *_hpLayoutBox;
    CCLayoutBox *_spLayoutBox;
    CCLayoutBox *_skillBox;
    CCSprite *_selectionHalo;
}

@property (nonatomic, strong, readonly) BGHeroButton *heroAvatar;
@property (nonatomic, strong, readonly) CCSprite *selectionHalo;

@property (nonatomic, strong) BGHeroCard *heroCard;

@property (nonatomic, copy, readonly) NSString *heroName;
@property (nonatomic, readonly) NSInteger hpLimit;
@property (nonatomic, readonly) NSUInteger spLimit;
@property (nonatomic, readonly) NSUInteger handSizeLimit;
@property (nonatomic, readonly) NSInteger healthPoint;
@property (nonatomic, readonly) NSUInteger skillPoint;
@property (nonatomic, strong, readonly) NSArray *heroSkills;

- (void)renderHeroWithHeroId:(NSInteger)heroId;
- (void)updateHealthPointWithCount:(NSInteger)count;
- (void)updateSkillPointWithCount:(NSUInteger)count;

- (void)enableHeroAvatar;
- (void)disableHeroAvatar;
- (void)disableHeroAvatarAndRemoveSkills;
- (void)enableHeroSkillWithSkillIds:(NSArray *)skillIds;
- (void)setHeroSkillsVisible:(BOOL)isVisible;
- (void)checkTargetPlayerEnablement;

- (BGHeroSkill *)heroSkillBySkillId:(BGHeroSkillId)skillId;

- (void)heroAvatarTouched:(BGHeroButton *)heroButton;
- (void)selectTargetPlayer;

- (void)resetHeroSkill;

@end
