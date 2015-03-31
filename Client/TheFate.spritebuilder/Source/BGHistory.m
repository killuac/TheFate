//
//  BGHistory.m
//  FateClient
//
//  Created by Killua Liu on 3/9/14.
//
//

#import "BGHistory.h"
#import "BGGameScene.h"

@implementation BGHistory {
    BGAction _action;
}

@synthesize text = _text;

static NSDictionary *actionHistory;

- (id)init
{
    if (self = [super init]) {
        _action = self.game.action;
        _dateTime = [NSDate date];
        
        switch (_action) {
            case ActionStartRound:
                _text = actionHistory[@(_action).stringValue];
                break;
                
            default:
                break;
        }
    }
    return self;
}

- (BGGameScene *)game
{
    return [CCDirector sharedDirector].runningScene.children.lastObject;
}


+ (id)historyOfPlayer:(BGPlayer *)player withEsObject:(EsObject *)esObj
{
    return [[self alloc] initOfPlayer:player withEsObject:esObj];
}

- (id)initOfPlayer:(BGPlayer *)player withEsObject:(EsObject *)esObj
{
    if (!actionHistory) {
        NSString *path = [[NSBundle mainBundle] pathForResource:kPlistActionHistory ofType:kFileTypePLIST];
        actionHistory = [NSDictionary dictionaryWithContentsOfFile:path];
    }
    
    if (self = [super init]) {
        _player = player;
        _action = player.game.action;
        _dateTime = [NSDate date];
        
        id textObj = actionHistory[@(_action).stringValue];
        if (!textObj) return self;
        
        _text = textObj;
        NSMutableArray *parameters = [NSMutableArray array];
        NSUInteger cardCount = [esObj intWithKey:kParamCardCount];
        NSArray *cardIds = [esObj intArrayWithKey:kParamCardIdList];
        
        switch (_action) {
            case ActionStartTurn:
                _isStartTurn = YES;
                break;
                
            case ActionDrawCard:
				[parameters addObject:@(cardCount).stringValue];
				break;
				
			case ActionOkayToDiscard:
                if (cardIds.count > 0) {
                    [parameters addObject:@(cardIds.count).stringValue];
                } else {
                    _text = @"";
                }
				break;
                
            case ActionUseHandCard:
			case ActionChoseCardToUse:
			case ActionChoseCardToDrop:
			case ActionChoseTargetPlayer:
			case ActionOkay:
                if (player.heroSkill) {
                    _isShowTip = YES;
					_text = player.heroSkill.historyText;
                    if (cardIds.count > 0) [parameters addObject:@(cardIds.count).stringValue]; // 用几张牌来发动技能
				}
                if (player.equipmentCard) {     // 先发动了主动技能，后使用装备散失之刃
                    _isShowTip = YES;
					_text = player.equipmentCard.historyText;
                    if (cardIds.count > 0) [parameters addObject:@(cardIds.count).stringValue];
				}
                if (!player.heroSkill && !player.equipmentCard) {
					if (ActionUseHandCard == player.game.action) {                          // 正常使用卡牌
						if (player.firstUsedCard.isEquipment) {
							NSString *tarHeroName = (player.targetPlayer) ? player.targetPlayer.heroName : HERO_NAME_SELF;
                            [parameters addObject:tarHeroName];
						} else {
                            _text = player.firstUsedCard.historyText;
							[self addTargetHeroNameToParameters:parameters];
                        }
					}
                    else if (ActionOkay == player.game.action){
                        _isShowTip = YES;
						_text = (player.isStrengthening) ? [textObj firstObject] : @"";     // 强化了该卡牌
					}
                    else if (ActionChoseCardToUse == player.game.action) {
                        if (cardIds.count > 0) [parameters addObject:@(cardIds.count).stringValue]; // 用几张药膏救人
                    }
				}
                break;
                
            case ActionUseHeroSkill:    // 直接使用技能(不需要指定目标，也不需要卡牌)
				if (player.heroSkill.isSimple) {
                    _isShowTip = YES;
					_text = player.heroSkill.historyText;
				} else {
                    _text = @"";
                }
				break;
                
            case ActionPlayerSkillOrEquipmentTriggered: {
                _isShowTip = YES;
                NSInteger skillId = [esObj intWithKey:kParamHeroSkillId];
                NSInteger equipId = [esObj intWithKey:kParamEquipmentId];
                if (skillId != HeroSkillNull) {
                    _text = [player.character heroSkillBySkillId:skillId].historyText;
                } else if (equipId != HeroSkillNull) {
                    _text = [player.equipment equipmentByCardId:equipId].historyText;
                }
                break;
            }
				
			case ActionChoseCardToGet:
				if (0 == cardIds.count) {
					_text = [textObj firstObject];  // 抽取手牌
				} else {
					_text = [textObj lastObject];	// 抽取装备
				}
                if (0 == _player.targetPlayers.count) {     // 被贪婪的玩家抽取TurnOwner的手牌
                    [_player.targetPlayerNames addObject:_player.game.turnOwner.playerName];
                }
			case ActionChoseCardToGive:
			case ActionChoseCardToRemove:
				[self addTargetHeroNameToParameters:parameters];
				cardCount = (cardCount > 0) ? cardCount : cardIds.count;
                [parameters addObject:@(cardCount).stringValue];
				break;
				
			case ActionChoseColor: {
                _isShowTip = YES;
                NSString *colorText = [BGPlayingCard colorTextByColor:[esObj intWithKey:kParamSelectedColor]];
                [parameters addObject:colorText];
				break;
            }
				
			case ActionChoseSuits: {
                _isShowTip = YES;
                NSString *suitsText = [BGPlayingCard suitsTextBySuits:[esObj intWithKey:kParamSelectedSuits]];
				[parameters addObject:suitsText];
				break;
            }
				
			case ActionTableShowAllComparedCards: {
                NSUInteger idx = [_player.game.allPlayers indexOfObject:_player];
                NSArray *sortedCardIds = [_player.game.table sortComparedCardIds:cardIds];
				[self addCardTextWithCardIds:@[sortedCardIds[idx]] toParameters:parameters];
				break;
            }
				
			case ActionPlayerUpdateHero: {
                NSInteger hp = [esObj intWithKey:kParamHeroHealthPoint];
                NSInteger sp = [esObj intWithKey:kParamHeroSkillPoint];
				int32_t hpChanged = [esObj intWithKey:kParamHeroHpChanged];
				int32_t spChanged = [esObj intWithKey:kParamHeroSpChanged];
                BOOL isSunder = [esObj boolWithKey:kParamIsSunder];
                
                if (hpChanged == 0 && spChanged == 0) _text = @"";
                if (hpChanged < 0) _text = (spChanged > 0) ? textObj[0] : textObj[1];
				if (hpChanged > 0) _text = textObj[2];
				if (spChanged > 0 && hpChanged >= 0) _text = textObj[3];
				if (spChanged < 0) _text = (player.isTurnOwner) ? textObj[4] : textObj[5];
                
                if (isSunder) {
                    _text = textObj[6];
                    [parameters addObjectsFromArray:@[@(hp).stringValue, @(sp).stringValue]];
                }
                
                if (abs(hpChanged) > 0) [parameters addObject:@(abs(hpChanged)).stringValue];
                if (abs(spChanged) > 0) [parameters addObject:@(abs(spChanged)).stringValue];
				break;
            }
            
            case ActionChoseViewPlayerRole:
                _isShowTip = YES;
                break;
                
            default:
                break;
        }
        
        [self addCardTextWithCardIds:cardIds toParameters:parameters];
        [self addTargetHeroNameToParameters:parameters];
        _text = [BGUtil textWith:_text parameters:parameters];
    }
    
    return self;
}

- (void)addCardTextWithCardIds:(NSArray *)cardIds toParameters:(NSMutableArray *)parameters
{
    NSString *conText = @"";
    for (NSNumber *number in cardIds) {
        BGPlayingCard *card = [BGPlayingCard cardWithCardId:number.integerValue];
        conText = [NSString stringWithFormat:@"%@%@%@", conText, DELIMITER_SIGN, card.cardFullText];
    }
    if (conText.length > 0) [parameters addObject:[conText substringFromIndex:1]];
}

- (void)addTargetHeroNameToParameters:(NSMutableArray *)parameters
{
    NSString *conHeroName = @"";
    for (BGPlayer *player in _player.targetPlayers) {
        conHeroName = [NSString stringWithFormat:@"%@%@%@", conHeroName, DELIMITER_SIGN, player.heroName];
    }
    if (conHeroName.length > 0) [parameters addObject:[conHeroName substringFromIndex:1]];
}

- (NSString *)text
{
    return [_text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
}

- (NSString *)heroName
{
    return _player.heroName;
}

@end
