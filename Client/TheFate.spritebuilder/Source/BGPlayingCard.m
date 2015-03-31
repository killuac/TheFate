//
//  BGPlayingCard.m
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGPlayingCard.h"
#import "BGConstant.h"

@implementation BGPlayingCard

static NSArray *cardIdList, *cardList;

@synthesize cardColor = _cardColor;

- (id)initWithCardId:(NSInteger)cardId
{
    if (self = [super initWithCardId:cardId]) {
//      Get playing card enumeration/figure/suits by card id
        if (!cardIdList) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistPlayingCardIds ofType:kFileTypePLIST];
            cardIdList = [NSArray arrayWithContentsOfFile:path];
        }
        NSDictionary *cardInfo = cardIdList[cardId];
        
        _cardEnum = [cardInfo[kCardEnum] integerValue];
        _cardFigure = [cardInfo[kCardFigure] integerValue];
        _cardSuits = [cardInfo[kCardSuits] integerValue];
        
//      Read playing card detail information by card enumeration
        if (!cardList) {
            NSString *path = [[NSBundle mainBundle] pathForResource:kPlistPlayingCardList ofType:kFileTypePLIST];
            cardList = [NSArray arrayWithContentsOfFile:path];
        }
        cardInfo = cardList[_cardEnum];
        
        _cardName = cardInfo[kCardName];
        _cardText = cardInfo[kCardText];
        _cardDesc = cardInfo[kCardDesc];
        _cardType = [cardInfo[kCardType] integerValue];
        _targetCount = [cardInfo[kTargetCount] unsignedIntegerValue];
        _damageValue = [cardInfo[kDamageValue] unsignedIntegerValue];
        
        _isStrengthenable = [cardInfo[kIsStrengthenable] boolValue];
        _requiredSp = [cardInfo[kRequiredSp] integerValue];
        
        _equipmentType = [cardInfo[kEquipmentType] integerValue];
        _plusDistance = [cardInfo[kPlusDistance] unsignedIntegerValue];
        _minusDistance = [cardInfo[kMinusDistance] integerValue];
        _attackRange = [cardInfo[kAttackRange] unsignedIntegerValue];
        _isActiveLaunchable = [cardInfo[kIsActiveLaunchable] boolValue];
        _isEquippedOne = [cardInfo[kIsEquippedOne] boolValue];
        _transformedCardId = [cardInfo[kTransformedCardId] integerValue];
        _minHandCardCount = [cardInfo[kMinHandCardCount] unsignedIntegerValue];
        
        _tipText = cardInfo[kTipText];
        _strenTipText = cardInfo[kStrenTipText];
        _targetTipText = cardInfo[kTargetTipText];
        _dispelTipText = cardInfo[kDispelTipText];
        _useTipText = cardInfo[kUseTipText];
        _triggerTipText = cardInfo[kTriggerTipText];
        
        _historyText = cardInfo[kHistoryText];
        _historyText2 = cardInfo[kHistoryText2];
    }
    return self;
}

+ (id)cardWithCardId:(NSInteger)cardId
{
    return [[self alloc]initWithCardId:cardId];
}

+ (NSMutableArray *)playingCardsByCardIds:(NSArray *)cardIds
{
    NSMutableArray *cards = [NSMutableArray arrayWithCapacity:cardIds.count];
    for (NSNumber *cardId in cardIds) {
        [cards addObject:[BGPlayingCard cardWithCardId:cardId.integerValue]];
    }
    return cards;
}

+ (NSMutableArray *)playingCardIdsByCards:(NSArray *)cards
{
    NSMutableArray *cardIds = [NSMutableArray arrayWithCapacity:cards.count];
    for (BGPlayingCard *card in cards) {
        [cardIds addObject:@(card.cardId)];
    }
    return cardIds;
}

- (BGCardColor)cardColor
{
    if (CardSuitsHearts == _cardSuits || CardSuitsDiamonds == _cardSuits) {
        return CardColorRed;
    } else {
        return CardColorBlack;
    }
}

- (BOOL)isTargetable
{
    return (_targetCount > 0);
}

- (BOOL)isDispellable
{
    return (CardTypeMagic == _cardType || PlayingCardDiffusalBlade == _cardEnum);
}

- (BOOL)isDispel
{
    return (PlayingCardDispel == _cardEnum);
}

- (BOOL)isAttack
{
    return (PlayingCardNormalAttack == _cardEnum ||
            PlayingCardFlameAttack  == _cardEnum ||
            PlayingCardChaosAttack  == _cardEnum);
}

- (BOOL)isMislead
{
    return (PlayingCardMislead == _cardEnum);
}

- (BOOL)isWildAxe
{
    return (PlayingCardWildAxe == _cardEnum);
}

- (BOOL)isRedColor
{
    return (CardColorRed == self.cardColor);
}

- (BOOL)isEquipment
{
    return (CardTypeEquipment == _cardType);
}

#pragma mark - Image and font
- (NSString *)imageName
{
    NSString *imageName = [NSString stringWithFormat:@"PlayingCard/%@", _cardName];
    return [imageName stringByAppendingPathExtension:kFileTypePNG];
}

- (NSString *)avatarImageName
{
    if (CardTypeEquipment == _cardType) {
        NSString *imageName = [NSString stringWithFormat:@"EquipmentAvatar/%@", _cardName];
        return [imageName stringByAppendingPathExtension:kFileTypePNG];
    }
    return nil;
}

// Need 2/3/4 characters font file
- (NSString *)avatarFontName
{
    if (_cardText.length >= 4) {
        return FILE_FULL_NAME(kFontEquipmentName, _cardText.length, kFileTypeFNT);
    } else {
        return [kFontEquipmentName stringByAppendingPathExtension:kFileTypeFNT];
    }
}

#pragma mark - Tip text
+ (NSString *)colorTextByColor:(BGCardColor)color
{
    return (CardColorRed == color) ? COLOR_RED : COLOR_BLACK;
}

+ (NSString *)suitsTextBySuits:(BGCardSuits)suits
{
    switch (suits) {
        case CardSuitsSpades:
            return SUITS_SPADES;
        case CardSuitsHearts:
            return SUITS_HEARTS;
        case CardSuitsClubs:
            return SUITS_CLUBS;
        case CardSuitsDiamonds:
            return SUITS_DIAMONDS;
            break;
        default:
            return @"";
    }
}

- (NSString *)figureDisplayedText
{
    switch (_cardFigure) {
        case CardFigure1:
            return @"A";
            
        case CardFigure11:
            return @"J";
            
        case CardFigure12:
            return @"Q";
            
        case CardFigure13:
            return @"K";
            
        default:
            return @(_cardFigure).stringValue;
    }
}

- (NSString *)suitsDisplayedText
{
    switch (_cardSuits) {
        case CardSuitsSpades:
            return @"♠";
        case CardSuitsHearts:
            return @"♥";
        case CardSuitsClubs:
            return @"♣";
        case CardSuitsDiamonds:
            return @"♦";
            break;
        default:
            return @"";
    }
}

- (NSString *)cardFullText
{
    return [NSString stringWithFormat:@"%@(%@%@)", _cardText, [self suitsDisplayedText], [self figureDisplayedText]];
}

@end
