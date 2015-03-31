//
//  BGRoleCard.m
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGRoleCard.h"

@implementation BGRoleCard

- (id)initWithCardId:(NSInteger)cardId
{
    if (self = [super initWithCardId:cardId]) {
        NSString *path = [[NSBundle mainBundle] pathForResource:kPlistRoleCardList ofType:kFileTypePLIST];
        NSArray *cardList = [NSArray arrayWithContentsOfFile:path];
        NSDictionary *cardInfo = cardList[cardId];
        
        _cardEnum = [cardInfo[kCardEnum] integerValue];
        _cardName = cardInfo[kCardName];
        _cardText = cardInfo[kCardText];
    }
    return self;
}

+ (id)cardWithCardId:(NSInteger)aCardId
{
    return [[self alloc]initWithCardId:aCardId];
}

- (NSString *)imageName
{
    NSString *imageName = [NSString stringWithFormat:@"GameArtwork/Role%@", _cardName];
    return [imageName stringByAppendingPathExtension:kFileTypePNG];
}

- (NSString *)deathImageName
{
    NSString *imageName = [NSString stringWithFormat:@"GameArtwork/Dead%@", _cardName];
    return [imageName stringByAppendingPathExtension:kFileTypePNG];
}

@end
