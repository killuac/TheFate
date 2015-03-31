//
//  BGCard.m
//  FateClient
//
//  Created by Killua Liu on 6/30/13.
//
//

#import "BGCard.h"
#import "BGPlayingCard.h"

@implementation BGCard

- (id)initWithCardId:(NSInteger)cardId
{
    if (cardId <= 0) return nil;
    
    if (self = [super init]) {
        _cardId = cardId;
    }
    return self;
}

+ (id)cardWithCardId:(NSInteger)cardId
{
    return [[self alloc] initWithCardId:cardId];
}

- (BOOL)isEqual:(id)object
{
    return (_cardId == [object cardId]);
}

- (BOOL)isPlayingCard
{
    return [self isKindOfClass:[BGPlayingCard class]];
}

- (NSString *)cardDesc
{
    return [_cardDesc stringByReplacingOccurrencesOfString:@"\\n" withString:@"\n"];
}

- (NSString *)imageName
{
    return [_cardName stringByAppendingPathExtension:kFileTypePNG];
}

- (NSString *)verticalText
{
    NSString *text = [NSString string];
    for (NSUInteger i = 0; i < _cardText.length; i++) {
        text = [text stringByAppendingFormat:@"\n%@", [_cardText substringWithRange:NSMakeRange(i, 1)]];
    }
    
    return [text substringFromIndex:1];
}

@end
