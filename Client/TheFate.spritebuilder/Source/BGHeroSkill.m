//
//  BGHeroSkill.m
//  FateClient
//
//  Created by Killua Liu on 7/29/13.
//
//

#import "BGHeroSkill.h"
#import "BGGameScene.h"

@implementation BGHeroSkill

static NSArray *skillList;

- (id)initWithSkillId:(BGHeroSkillId)skillId ofPlayer:(BGPlayer *)player
{
    if (skillId <= 0) return nil;
    
    if (self = [super init]) {
        _player = player;
        if (!skillList) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistHeroSkillList ofType:kFileTypePLIST];
            skillList = [NSArray arrayWithContentsOfFile:path];
        }
        NSDictionary *skillInfo = skillList[skillId];
        
        _skillId = skillId;
        _skillName = skillInfo[kSkillName];
        _skillText = skillInfo[kSkillText];
        _skillDesc = skillInfo[kSkillDesc];
        _skillCategory = [skillInfo[kSkillCategory] integerValue];
        _skillType = [skillInfo[kSkillType] integerValue];
        _isDispellable = [skillInfo[kIsDispellable] boolValue];
        _targetCount = [skillInfo[kTargetCount] unsignedIntegerValue];
        _maxTargetCount = [skillInfo[kMaxTargetCount] unsignedIntegerValue];
        _minHandCardCount = [skillInfo[kMinHandCardCount] unsignedIntegerValue];
        
        if ([skillInfo[kTransformedCardId] isKindOfClass:[NSNumber class]]) {
            _transformedCardId = [skillInfo[kTransformedCardId] integerValue];
        } else {
            _transformedCardId = [skillInfo[kTransformedCardId][0] integerValue];
            _transformedCardId2 = [skillInfo[kTransformedCardId][1] integerValue];
        }
        
        _triggerReasons = skillInfo[kTriggerReasons];
        
        _useTipText = skillInfo[kUseTipText];
        _triggerTipText = skillInfo[kTriggerTipText];
        _yesTipText = skillInfo[kYesTipText];
        _targetTipText = skillInfo[kTargetTipText];
        _dispelTipText = skillInfo[kDispelTipText];
        
        _historyText = skillInfo[kHistoryText];
        _historyText2 = skillInfo[kHistoryText2];
    }
    return self;
}

+ (id)heroSkillWithSkillId:(BGHeroSkillId)skillId ofPlayer:(BGPlayer *)player
{
    BGHeroSkill *skill = [[self alloc] initWithSkillId:skillId ofPlayer:player];
    NSString *className = [kClassPrefix stringByAppendingString:skill.skillName];
    id dynSkill = [[NSClassFromString(className) alloc] initWithSkillId:skill.skillId ofPlayer:player];
    return (dynSkill) ? dynSkill : skill;
}

+ (NSMutableArray *)heroSkillsBySkillIds:(NSArray *)skillIds ofPlayer:(BGPlayer *)player
{
    NSMutableArray *skills = [NSMutableArray arrayWithCapacity:skillIds.count];
    for (NSNumber *skillId in skillIds) {
        [skills addObject:[BGHeroSkill heroSkillWithSkillId:skillId.integerValue ofPlayer:player]];
    }
    return skills;
}

+ (NSMutableArray *)heroSkillIdsBySkills:(NSArray *)skills
{
    NSMutableArray *skillIds = [NSMutableArray arrayWithCapacity:skills.count];
    [skills enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [skillIds addObject:@([obj skillId])];
    }];
    return skillIds;
}

- (BOOL)isTargetable
{
    return (_targetCount > 0);
}

- (BOOL)isActive
{
    return (SkillCategoryActive == _skillCategory);
}

- (BOOL)isSimple
{
    return (0 == _targetCount && 0 == _minHandCardCount);
}

#pragma mark - Tip text
- (NSString *)targetTipText
{
    return [BGUtil textWith:_targetTipText parameters:self.player.game.tipParameters];
}

- (NSString *)dispelTipText
{
    return [BGUtil textWith:_dispelTipText parameters:self.player.game.tipParameters];
}

#pragma mark - protocol implementation
- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (!player.isMe);
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return NO;
}

- (void)checkHandCardEnablement
{
    
}

#pragma mark - Sound
- (NSString *)soundName
{
    NSString *soundName = [NSString stringWithFormat:@"Sounds/HeroSkill/%@", _skillName];
    return [soundName stringByAppendingPathExtension:kFileTypeCAF];
}

- (void)playSound
{
    _isSoundPlayed = YES;
    [[BGAudioManager sharedAudioManager] playVoice:self.soundName];
}

@end
