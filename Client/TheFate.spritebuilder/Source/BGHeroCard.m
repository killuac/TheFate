//
//  BGHeroCard.m
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGHeroCard.h"

@implementation BGHeroCard

static NSArray *cardList;
+ (NSArray *)cardList
{
    if (!cardList) {
        NSString *path = [[NSBundle mainBundle] pathForResource:kPlistHeroCardList ofType:kFileTypePLIST];
        cardList = [NSArray arrayWithContentsOfFile:path];
    }
    return cardList;
}

- (id)initWithCardId:(NSInteger)cardId
{
    if (self = [super initWithCardId:cardId]) {
        cardList = self.class.cardList;
        NSDictionary *cardInfo = cardList[cardId];
        
        _cardName = cardInfo[kCardName];
        _cardText = cardInfo[kCardText];
        _cardDesc = cardInfo[kCardDesc];
        
        _attribute = [cardInfo[kAttribute] integerValue];
        _hpLimit = [cardInfo[kHpLimit] integerValue];
        _spLimit = [cardInfo[kSpLimit] unsignedIntegerValue];
        _handSizeLimit = [cardInfo[kHandSizeLimit] unsignedIntegerValue];
        _skillIds = cardInfo[kSkillIds];
        _gender = [cardInfo[kGender] integerValue];
        
        _price = [cardInfo[kPrice] unsignedIntegerValue];
    }
    
    return self;
}

+ (id)cardWithCardId:(NSInteger)aCardId
{
    return [[self alloc]initWithCardId:aCardId];
}

+ (NSArray *)heroCardsWithHeroIds:(NSArray *)heroIds
{
    NSMutableArray *heroCards = [NSMutableArray arrayWithCapacity:heroIds.count];
    [heroIds enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        BGCard *heroCard = [BGHeroCard cardWithCardId:[obj integerValue]];
        [heroCards addObject:heroCard];
    }];
    
    return heroCards;
}

+ (NSArray *)saleHeroCards
{
    NSMutableArray *heroCards = [NSMutableArray array];
    for (NSUInteger i = 1; i < self.class.cardList.count; i++) {
        BGHeroCard *card = [BGHeroCard cardWithCardId:i];
        if (card.price > 0) [heroCards addObject:card];
    }
    return heroCards;
}

#pragma mark - Image, font and sound
- (NSString *)imageName
{
    NSString *imageName = [NSString stringWithFormat:@"HeroCard/%@", _cardName];
    return [imageName stringByAppendingPathExtension:kFileTypePNG];
}

- (NSString *)avatarImageName
{
    NSString *imageName = [NSString stringWithFormat:@"HeroAvatar/%@", _cardName];
    return [imageName stringByAppendingPathExtension:kFileTypePNG];
}

- (NSString *)attrImageName
{
    NSString *imageName = @"";
    switch (_attribute) {
        case HeroAttributeAgility:
            imageName = kImageCountFrameAgility;
            break;
            
        case HeroAttributeIntelligence:
            imageName = kImageCountFrameIntelligence;
            break;
            
        case HeroAttributeStrength:
            imageName = kImageCountFrameStrength;
            break;
    }
    
    return imageName;
}

// Need 2/3/4 characters font file
- (NSString *)avatarFontName
{
    if (_cardText.length >= 4) {
        return FILE_FULL_NAME(kFontHeroName, _cardText.length, kFileTypeFNT);
    } else {
        return [kFontHeroName stringByAppendingPathExtension:kFileTypeFNT];
    }
}

#pragma mark - Sound
- (void)playSound
{
    NSString *soundName = [NSString stringWithFormat:@"Sounds/HeroDeath/%@", _cardName];
    soundName = [soundName stringByAppendingPathExtension:kFileTypeWAV];
    
    [(BGAudioManager *)[BGAudioManager sharedAudioManager] playSoundEffect:soundName];
}

@end
