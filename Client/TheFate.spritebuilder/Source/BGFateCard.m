//
//  BGFateCard.m
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGFateCard.h"

@implementation BGFateCard

- (id)initWithCardId:(NSInteger)cardId
{
    if (self = [super initWithCardId:cardId]) {        
        NSString *path = [[NSBundle mainBundle] pathForResource:kPlistFateCardList ofType:kFileTypePLIST];
        NSArray *cardList = [NSArray arrayWithContentsOfFile:path];
        NSDictionary *cardInfo = cardList[cardId];
        
        _cardName = cardInfo[kCardName];
        _cardText = cardInfo[kCardText];
        _cardDesc = cardInfo[kCardDesc];
    }
    return self;
}

+ (id)cardWithCardId:(NSInteger)aCardId
{
    return [[self alloc] initWithCardId:aCardId];
}

- (NSString *)imageName
{
    return [_cardName stringByAppendingPathExtension:kFileTypeJPG];
}

@end
