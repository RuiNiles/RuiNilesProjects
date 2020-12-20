import pygame
import copy
import random

#initialize pygame
pygame.init
pygame.font.init()

#initialize music and set volume
pygame.mixer.init()
pygame.mixer.music.load("C:/Users/ruini/Documents/Portfolio/tetris/Sounds/tetris.mp3")
pygame.mixer.music.play(-1)
pygame.mixer.music.set_volume(0.2)

#set window height and width
window_height = 840
window_width = 650 

#select font
myfont = pygame.font.SysFont('Roboto', 35)

#initialize clock
clock = pygame.time.Clock()

#set up window with a caption
win = pygame.display.set_mode((window_width, window_height))
pygame.display.set_caption("Tetris")

#set starting score and number of lines
level = 1
score = 0
lines = 0

#set Tetris grid height and width
grid_height = 24
grid_width = 10

#create empty grid that landed tetrominos will be added to
landed = []
for i in range(grid_height):
    row = []
    for x in range(grid_width):
        row.append(0)
    landed.append(row)

#copy landed grid to the working grid i.e the grid that contains the moving tetromino
grid = copy.deepcopy(landed)

#create smaller grid to display the held tetromino in
emptySpareGrid = []
for i in range(5):
    row = []
    for x in range(5):
        row.append(0)
    emptySpareGrid.append(row)

#copy the empty version of the grid to the working version of the grid
spareGrid = copy.deepcopy(emptySpareGrid) 

#all tetrominos and rotations
I_tetromino = [[[1], [1], [1], [1]],[[1,1,1,1]]]  
O_tetromino = [[[2,2],[2,2]]]
S_tetromino = [[[0,3,3],[3,3,0]],[[3,0],[3,3],[0,3]]]
Z_tetromino = [[[4,4,0],[0,4,4]],[[0,4],[4,4],[4,0]]]
L_tetromino = [[[5,0],[5,0],[5,5]],[[5,5,5],[5,0,0]],[[5,5],[0,5],[0,5]],[[0,0,5],[5,5,5]]]
J_tetromino = [[[0,6],[0,6],[6,6]],[[6,0,0],[6,6,6]],[[6,6],[6,0],[6,0]],[[6,6,6],[0,0,6]]]
T_tetromino = [[[7,7,7],[0,7,0]],[[0,7],[7,7],[0,7]],[[0,7,0],[7,7,7]],[[7,0],[7,7],[7,0]]]


#class definition of a tetromino
class Tetromino:
    #tetrominos attributes
    dimensions = []
    position_x = 0
    position_y = 0
    all_rotations = []

    #constructor
    def __init__(self, x, y, r):
        self.all_rotations = r  
        self.dimensions = r[0]
        self.position_x = x
        self.position_y = y
    
    #getter methods:
    def get_position_x(self):
        return self.position_x   

    def get_position_y(self):
        return self.position_y   

    def get_dimensions(self):
        return self.dimensions

    #get the width of the widest point of the tetromino
    def tetromino_width(self):
        width = len(self.dimensions[0])
        for i in self.dimensions:
            if len(i) > width:
                width = len(i)
        return width
    
    #change rotation of the tetromino
    def rotate(self, direction):
        index = self.all_rotations.index(self.dimensions) + direction
        if index > len(self.all_rotations) - 1:
            index = 0
        if index < 0:
            index = len(self.all_rotations) - 1
        self.dimensions = self.all_rotations[index]
        if self.position_x + self.tetromino_width() > 10:
            self.position_x -= self.tetromino_width() - 1

        if self.position_y + len(self.dimensions) > 24:
            self.position_y -= len(self.dimensions) - 1

        
#add a tetromino to a grid space
def add_tetromino(x, y, dimensions, grid_space):
    origin_x = x
    origin_y = y
    count_y = 0
    for i in dimensions:
        count_x = 0
        for z in i:
            if z != 0:
                 grid_space[origin_y + count_y][origin_x + count_x] = z  
            count_x += 1
        count_y += 1

#get length for collision detection
def true_lenght(dim, x):
    length = len(dim)
    found = False
    count = 1
    while found == False:
        if dim[len(dim) - count][x] != 0:
            found = True
        else:
            length -= 1
            count += 1
    return length  

#get widths for collision detection:
def true_width_right(dim, y, start_width):
    width = start_width
    found = False
    count = 1
    while found == False:
        if dim[y][start_width - count] != 0:
            found = True
        else:
            width -= 1
            count += 1
    return width

def true_width_left(dim, y):
    width = 0
    found = False
    count = 0
    while found == False:
        if dim[y][count] != 0:
            found = True
        else:
            width += 1
            count += 1
    return width

#check for collisions below the current moving tetromino
def check_collision_down(tetromino):
    clear = True
    if current_tetromino.position_y + len(current_tetromino.dimensions) < 24:
        for i in range(tetromino.tetromino_width()):
            if landed[tetromino.position_y + true_lenght(tetromino.dimensions,i)][tetromino.position_x + i] != 0:
                clear = False
    return clear

#check for collisions to the side of the current moving tetromino
def check_collision_side(tetromino, side):
    count = 0
    clear = True
    if(side == -1):   
        for i in tetromino.dimensions:
            if tetromino.position_x - 1 + true_width_left(tetromino.dimensions, count) >= 0:
                if(landed[tetromino.position_y + count][tetromino.position_x - 1 + true_width_left(tetromino.dimensions, count)] != 0):
                    clear = False
                count += 1 
           
        return clear
    else:
        for i in tetromino.dimensions:
            if tetromino.position_x + true_width_right(tetromino.dimensions, count,tetromino.tetromino_width()) <= grid_width - 1:
                if(landed[tetromino.position_y + count][tetromino.position_x + true_width_right(tetromino.dimensions, count,tetromino.tetromino_width())] != 0):
                    clear = False
                count += 1
        return clear

#draw the grids and scores into the window
def drawGrid(grid, offSet, height, width):
    if offSet == 0: 
        win.fill((0,0,0))   
    blockSize = 35
    for x in range(height):
        for y in range(width):
            rect = pygame.Rect(y*blockSize + (offSet + 1.5), x*blockSize + 1.5, blockSize - 2, blockSize - 2)
            if grid[x][y] == 1:
                pygame.draw.rect(win, (50,165,170), rect)
            elif grid[x][y] == 2:
                pygame.draw.rect(win, (220,235,40), rect)
            elif grid[x][y] == 3:
                pygame.draw.rect(win, (60,210,75), rect)
            elif grid[x][y] == 4:
                pygame.draw.rect(win, (210,30,20), rect)
            elif grid[x][y] == 5:
                pygame.draw.rect(win, (255,69,0), rect)
            elif grid[x][y] == 6:
                pygame.draw.rect(win, (30,25,180), rect)
            elif grid[x][y] == 7:
                pygame.draw.rect(win, (150,25,160), rect)
            else:
                rect = pygame.Rect(y*blockSize + offSet, x*blockSize, blockSize, blockSize)
                pygame.draw.rect(win, (200,200,200), rect, 1)
    if offSet == 0:           
        drawGrid(spareGrid, 400, 5, 5) 
        textsurface = myfont.render("level: " + str(level), False, (200, 200, 200))       
        win.blit(textsurface,(400,250)) 
        textsurface = myfont.render("lines: " + str(lines), False, (200, 200, 200))       
        win.blit(textsurface,(400,300)) 
        textsurface = myfont.render("Score: " + str(score), False, (200, 200, 200))       
        win.blit(textsurface,(400,350)) 
        textsurface = myfont.render("Controls: ", False, (200, 200, 200))       
        win.blit(textsurface,(400,500)) 
        textsurface = myfont.render("Arrow Keys: Move", False, (200, 200, 200))       
        win.blit(textsurface,(400,550))
        textsurface = myfont.render("Z/X: Rotate", False, (200, 200, 200))       
        win.blit(textsurface,(400,600))
        textsurface = myfont.render("C: Hold", False, (200, 200, 200))       
        win.blit(textsurface,(400,650))
        textsurface = myfont.render("W: Volume up", False, (200, 200, 200))       
        win.blit(textsurface,(400,700))
        textsurface = myfont.render("S: Volume down", False, (200, 200, 200))       
        win.blit(textsurface,(400,750))
        updateScores = False

    pygame.display.update()

#draws a numerical representation of the grid array onto the window
#for dev purposes
def writeNums():
    win.fill((0,0,0)) 
    for i in range(grid_height):
        textsurface = myfont.render(str(grid[i]), True, (200, 200, 200))
        win.blit(textsurface,(0,i * 25))    
        pygame.display.update()

#check grid for any full lines 
def checkLines():
    global lines 
    global nextLevel
    clearedLines = []
    for y in range(grid_height):
        x = 0
        while True: 
            if landed[y][x] == 0:
                break
            
            if x == grid_width - 1:
                lines += 1
                nextLevel += 1
                clearedLines.append(y)
                break
            x+=1

    if clearedLines != []:
        updateScore(len(clearedLines))
        doClear(clearedLines)

#update score depending on the number of lines cleared
def updateScore(num):
    global score
    if num == 1:
        score += 100 
    elif num == 2:
        score += 300 
    elif num == 3:
        score += 500
    elif num == 4:
        score += 800
    elif num > 4:
        score += int(num / 4) * 800
        score += (num % 4) * 100

#erase cleared lines from grid and move remaining lines down into free space
def doClear(clearedLines):
    for y in clearedLines:
        for x in range(grid_width):
            landed[y][x] = 0

    grid = copy.deepcopy(landed)
    drawGrid(grid, 0, grid_height, grid_width) #show line disappearing animation
    pygame.time.delay(500)
    #play sound effect
    lineClear = pygame.mixer.Sound("C:/Users/ruini/Documents/Portfolio/tetris/Sounds/tetris_line_clear.mp3")
    lineClear.set_volume(pygame.mixer.music.get_volume())
    lineClear.play()
    for y in clearedLines:
        for i in range(y, 1, -1):
            for x in range(grid_width):
                landed[i][x] = landed[i - 1][x]    
    
#pick next tetromino at random    
def getNextTetromino():
    return tetrominos[random.randint(0,6)]

#check if the player has lost and exit if they have
def checkLose():
    global run
    for x in grid[0]:
        if x != 0:
            run = False
    
#list of all availible tetrominos            
tetrominos = [I_tetromino, O_tetromino, S_tetromino, Z_tetromino, L_tetromino, J_tetromino, T_tetromino]
current_tetromino = Tetromino(4, 0, getNextTetromino())
spare_tetromino = Tetromino(4, 0, getNextTetromino())
add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)
add_tetromino(1, 1, spare_tetromino.dimensions, spareGrid)

#delay counters to used to time specific functions
tetromino_counter = 0
run = True
wait_time = 1
timer = wait_time
speed = 5
speed_count = speed
canSwitch = True
pressDelayTime = 3
pressDelay = pressDelayTime
nextLevel = 0

#main game loop:
while run:

    #exit loop if game is quit
    for event in pygame.event.get() :
        if event.type == pygame.QUIT:
            run = False
   
    if speed_count == 0:
        if current_tetromino.position_y + len(current_tetromino.dimensions) < 24 and check_collision_down(current_tetromino):
            current_tetromino.position_y += 1
            grid = copy.deepcopy(landed)
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)
        elif  timer > 0:
            timer -= 1
        else:
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, landed)
            tetromino_counter += 1
            timer = wait_time
            checkLines()
            checkLose()
            canSwitch = True
            current_tetromino = Tetromino(4, 0, getNextTetromino())
        speed_count = speed
    else:
        speed_count -= 1
    
    keys = pygame.key.get_pressed()

    if keys[pygame.K_LEFT] and current_tetromino.position_x > 0 and check_collision_side(current_tetromino, -1):
        current_tetromino.position_x -= 1
        grid = copy.deepcopy(landed)
        add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)

    if keys[pygame.K_RIGHT]:
        if current_tetromino.position_x + current_tetromino.tetromino_width() < 10 and check_collision_side(current_tetromino, 1):
            current_tetromino.position_x += 1
            grid = copy.deepcopy(landed)
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)

    if keys[pygame.K_x]:   
        if  check_collision_side(current_tetromino, -1) == True and check_collision_side(current_tetromino, 1) == True and check_collision_down(current_tetromino) == True and pressDelay == 0:
            current_tetromino.rotate(1)
            grid = copy.deepcopy(landed)
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)
            pressDelay = pressDelayTime

    if keys[pygame.K_z]:    
        if  check_collision_side(current_tetromino, -1) == True and check_collision_side(current_tetromino, 1) == True and check_collision_down(current_tetromino) == True and pressDelay == 0:
            current_tetromino.rotate(-1) 
            grid = copy.deepcopy(landed)
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)
            pressDelay = pressDelayTime

    if keys[pygame.K_DOWN]: 
        if current_tetromino.position_y + len(current_tetromino.dimensions) < 24 and check_collision_down(current_tetromino):
            current_tetromino.position_y += 1
            grid = copy.deepcopy(landed)
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, current_tetromino.dimensions, grid)
 
    if keys[pygame.K_c]: 
        if canSwitch: 
            current_tetromino.position_x = 4
            current_tetromino.position_y = 0
            spareGrid = copy.deepcopy(emptySpareGrid) 
            add_tetromino(current_tetromino.position_x, current_tetromino.position_y, spare_tetromino.dimensions, grid)
            add_tetromino(1, 1, current_tetromino.dimensions, spareGrid)
            temp = spare_tetromino
            spare_tetromino = current_tetromino
            current_tetromino = temp
            grid = copy.deepcopy(landed)
            drawGrid(grid, 0, grid_height, grid_width)
            canSwitch = False
       
    if keys[pygame.K_w]: 
        if pygame.mixer.music.get_volume() < 1:
            pygame.mixer.music.set_volume(pygame.mixer.music.get_volume() + 0.1)

    if keys[pygame.K_s]: 
        if pygame.mixer.music.get_volume() > 0:
            pygame.mixer.music.set_volume(pygame.mixer.music.get_volume() - 0.1)

    if pressDelay > 0:
        pressDelay -= 1 

    if nextLevel >= 5:
        level += 1
        nextLevel -= 5
        if speed > 0:
            speed -= 1

    drawGrid(grid, 0, grid_height, grid_width)
    #writeNums()
    clock.tick(20)
   
pygame.quit()