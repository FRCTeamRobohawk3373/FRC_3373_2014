# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4

import SimpleCV

DEBUG = True

screenSize = (800, 600)
if DEBUG: display = SimpleCV.Display(screenSize)

camURL = "http://10.33.73.167:80/jpg/image.jpg"

ratioLow = 1.1
ratioHigh = 1.6

def main():
    isHot = False
    distance = 0
    while (DEBUG and display.isNotDone()) or (DEBUG != True):

        img = SimpleCV.Image(camURL)

        greenDist = img.colorDistance(SimpleCV.Color.AQUAMARINE)
        filtered = img - greenDist

        blobs = filtered.findBlobs(minsize = 300)

        if blobs:
            rectangles = blobs.filter([b.isRectangle(0.3) for b in blobs])
            if rectangles:
                
                isHot = checkIsHot(rectangles, isHot)
                distance = getDistance(rectangles)

                if DEBUG:
                    drawingLayer = SimpleCV.DrawingLayer(screenSize)
                    drawRects(rectangles, filtered, drawingLayer)
                    debugPrint(rectangles, isHot, distance)
                    filtered.applyLayers()
                    
        if DEBUG: filtered.save(display)


def debugPrint(rectangles, isHot, distance):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        print("ratio: " + str(rec1H / rec2W))
    print("isHot: " + str(isHot))
    print("distance: " + str(distance))

def drawRects(rectangles, filtered, drawingLayer):
    drawingLayer.centeredRectangle(rectangles[-1].coordinates(), (rectangles[-1].width(), rectangles[-1].height()), SimpleCV.Color.YELLOW, 1, False, -1)
    if len(rectangles) > 1:
        drawingLayer.centeredRectangle(rectangles[-2].coordinates(), (rectangles[-2].width(), rectangles[-2].height()), SimpleCV.Color.YELLOW, 1, False, -1) 
    filtered.addDrawingLayer(drawingLayer)

def checkIsHot(rectangles, isHot):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        ratio = rec1H / rec2W

        isHot = (ratio > ratioLow) and (ratio < ratioHigh)
    else:
        isHot = False

    return isHot

def getDistance(rectangles):
    return str(rectangles[-1].height())

main()
