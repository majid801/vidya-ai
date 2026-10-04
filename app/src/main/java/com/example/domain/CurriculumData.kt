package com.example.domain

import com.example.data.model.ChapterInfo
import com.example.data.model.SubjectInfo

object CurriculumData {

    val AVAILABLE_GRADES = listOf(
        "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
        "Class 6", "Class 7", "Class 8", "Class 9", "Class 10",
        "Intermediate 1st Year", "Intermediate 2nd Year",
        "College / B.Tech 1st Year", "College / B.Tech 2nd Year",
        "College / B.Tech 3rd Year", "College / B.Tech 4th Year",
        "College / Degree (B.Com/B.Sc/BBA)"
    )

    val INTERMEDIATE_STREAMS = listOf(
        "MPC (Maths, Physics, Chemistry)",
        "BiPC (Biology, Physics, Chemistry)",
        "MEC (Maths, Economics, Commerce)",
        "CEC (Civics, Economics, Commerce)",
        "Computer Science & Engineering (B.Tech CSE)",
        "Data Science & Artificial Intelligence (AI/ML)",
        "Electronics & Communication (ECE)",
        "Commerce & Management (B.Com/BBA)",
        "General / Other"
    )

    val BOARDS = listOf(
        "CBSE",
        "ICSE / ISC",
        "Telangana State Board (TSBIE/SSC)",
        "Andhra Pradesh Board (BIEAP)",
        "Maharashtra State Board",
        "Karnataka State Board",
        "Other State Boards"
    )

    fun getSubjectsFor(grade: String, stream: String = ""): List<SubjectInfo> {
        return when {
            grade.contains("Class 1") || grade.contains("Class 2") -> listOf(
                createBasicMath(),
                createEVS(),
                createEnglishBasic()
            )
            grade.contains("Class 3") || grade.contains("Class 4") || grade.contains("Class 5") -> listOf(
                createPrimaryMath(),
                createGeneralScience(),
                createSocialStudiesPrimary(),
                createEnglishBasic()
            )
            grade.contains("Class 6") || grade.contains("Class 7") || grade.contains("Class 8") -> listOf(
                createMiddleMath(),
                createGeneralScienceMiddle(),
                createSocialScienceMiddle(),
                createEnglishMiddle()
            )
            grade.contains("Class 9") || grade.contains("Class 10") -> listOf(
                createSecondaryMath(),
                createPhysicsSecondary(),
                createChemistrySecondary(),
                createBiologySecondary(),
                createSocialScienceSecondary(),
                createEnglishSecondary()
            )
            grade.contains("Intermediate") -> {
                when {
                    stream.contains("MPC") -> listOf(
                        createInterMaths(),
                        createInterPhysics(),
                        createInterChemistry(),
                        createInterEnglish()
                    )
                    stream.contains("BiPC") -> listOf(
                        createInterBiology(),
                        createInterPhysics(),
                        createInterChemistry(),
                        createInterEnglish()
                    )
                    stream.contains("MEC") -> listOf(
                        createInterMaths(),
                        createInterEconomics(),
                        createInterCommerce(),
                        createInterEnglish()
                    )
                    stream.contains("CEC") -> listOf(
                        createInterCivics(),
                        createInterEconomics(),
                        createInterCommerce(),
                        createInterEnglish()
                    )
                    else -> listOf(
                        createInterMaths(),
                        createInterPhysics(),
                        createInterChemistry(),
                        createInterEnglish()
                    )
                }
            }
            grade.contains("College") || grade.contains("B.Tech") || grade.contains("Degree") -> {
                when {
                    stream.contains("Commerce") || stream.contains("B.Com") || stream.contains("BBA") -> listOf(
                        createInterCommerce(),
                        createInterEconomics(),
                        createDatabaseManagementSystems(),
                        createBusinessAndFinance()
                    )
                    stream.contains("Data Science") || stream.contains("AI") -> listOf(
                        createArtificialIntelligence(),
                        createDataStructuresAndAlgorithms(),
                        createDatabaseManagementSystems(),
                        createPythonProgramming()
                    )
                    else -> listOf(
                        createDataStructuresAndAlgorithms(),
                        createDatabaseManagementSystems(),
                        createOperatingSystems(),
                        createComputerNetworks(),
                        createObjectOrientedProgramming()
                    )
                }
            }
            else -> listOf(createSecondaryMath(), createPhysicsSecondary(), createChemistrySecondary())
        }
    }

    private fun createBasicMath() = SubjectInfo(
        id = "math_primary",
        name = "Mathematics",
        iconName = "calculate",
        colorHex = "#4F46E5",
        chapters = listOf(
            ChapterInfo("ch1", "Shapes and Space", 2, listOf("Inside Outside", "Bigger Smaller", "Rolling Sliding", "Shapes")),
            ChapterInfo("ch2", "Numbers 1 to 100", 3, listOf("Counting", "Addition with Pictures", "Subtraction Basics", "Place Values")),
            ChapterInfo("ch3", "Measurement and Money", 2, listOf("Longer Shorter", "Coins and Currency", "Time of Day"))
        )
    )

    private fun createEVS() = SubjectInfo(
        id = "evs_primary",
        name = "Environmental Studies (EVS)",
        iconName = "eco",
        colorHex = "#059669",
        chapters = listOf(
            ChapterInfo("evs1", "My Family & Myself", 2, listOf("Body parts", "Family tree", "Good habits")),
            ChapterInfo("evs2", "Plants & Animals Around Us", 2, listOf("Types of trees", "Domestic and wild animals", "Pet care")),
            ChapterInfo("evs3", "Seasons, Water & Air", 3, listOf("Summer, Winter, Monsoon", "Save Water", "Clean Air"))
        )
    )

    private fun createEnglishBasic() = SubjectInfo(
        id = "eng_basic",
        name = "English Language",
        iconName = "menu_book",
        colorHex = "#D97706",
        chapters = listOf(
            ChapterInfo("eng1", "Phonics & Vocabulary", 2, listOf("Vowels & Consonants", "Rhyming words", "Sight words")),
            ChapterInfo("eng2", "Grammar: Nouns & Verbs", 3, listOf("Naming words", "Action words", "Describing words")),
            ChapterInfo("eng3", "Reading Comprehension", 2, listOf("Short stories", "Moral fables", "Poetry recitation"))
        )
    )

    private fun createPrimaryMath() = SubjectInfo(
        id = "math_class5",
        name = "Mathematics",
        iconName = "calculate",
        colorHex = "#4F46E5",
        chapters = listOf(
            ChapterInfo("m5_1", "The Fish Tale (Large Numbers)", 3, listOf("Lakhs and Crores", "Speed and Distance", "Word Problems")),
            ChapterInfo("m5_2", "Shapes and Angles", 3, listOf("Right angles", "Acute & Obtuse", "Angles in names and clocks")),
            ChapterInfo("m5_3", "Fractions: Parts and Wholes", 4, listOf("Equivalent fractions", "Shading regions", "Fraction additions")),
            ChapterInfo("m5_4", "Area and Perimeter", 3, listOf("Square grid counting", "Rectangles & squares", "Perimeter calculations"))
        )
    )

    private fun createGeneralScience() = SubjectInfo(
        id = "sci_class5",
        name = "Science & EVS",
        iconName = "science",
        colorHex = "#0D9488",
        chapters = listOf(
            ChapterInfo("sci5_1", "Super Senses of Animals", 3, listOf("Sight, Smell, Hearing in animals", "Echolocation", "Sleeping patterns")),
            ChapterInfo("sci5_2", "From Tasting to Digesting", 3, listOf("Taste buds", "Digestive system path", "Nutrients & Glucose")),
            ChapterInfo("sci5_3", "Seeds and Seeds", 3, listOf("Germination stages", "Seed dispersal methods", "Pitcher plants")),
            ChapterInfo("sci5_4", "Experiments with Water", 2, listOf("Floating and Sinking", "Solubility", "The Dead Sea density"))
        )
    )

    private fun createSocialStudiesPrimary() = SubjectInfo(
        id = "sst_class5",
        name = "Social Studies",
        iconName = "public",
        colorHex = "#EA580C",
        chapters = listOf(
            ChapterInfo("sst5_1", "Our Earth and Continents", 3, listOf("Continents & Oceans", "Equator & Poles", "Globe reading")),
            ChapterInfo("sst5_2", "Indian Heritage & States", 3, listOf("Northern Mountains", "Peninsular Plateau", "Cultural Diversity"))
        )
    )

    private fun createMiddleMath() = SubjectInfo(
        id = "math_middle",
        name = "Mathematics",
        iconName = "calculate",
        colorHex = "#4F46E5",
        chapters = listOf(
            ChapterInfo("m8_1", "Rational Numbers", 3, listOf("Closure & Commutativity", "Representation on Number Line", "Additive Inverse")),
            ChapterInfo("m8_2", "Linear Equations in One Variable", 4, listOf("Solving multi-step equations", "Word problems", "Variable on both sides")),
            ChapterInfo("m8_3", "Squares, Cubes & Roots", 3, listOf("Prime factorisation method", "Long division square roots", "Cube estimation")),
            ChapterInfo("m8_4", "Algebraic Expressions & Identities", 4, listOf("(a+b)^2 identity", "(a-b)^2 identity", "(a+b)(a-b) identity", "Factorisation"))
        )
    )

    private fun createGeneralScienceMiddle() = SubjectInfo(
        id = "sci_middle",
        name = "Science",
        iconName = "science",
        colorHex = "#059669",
        chapters = listOf(
            ChapterInfo("sc8_1", "Crop Production & Management", 3, listOf("Kharif and Rabi crops", "Irrigation methods", "Manures vs Fertilizers")),
            ChapterInfo("sc8_2", "Force and Pressure", 4, listOf("Contact & Non-contact forces", "Pressure = F/A", "Atmospheric pressure")),
            ChapterInfo("sc8_3", "Friction & Sound", 3, listOf("Types of friction", "Sound propagation in media", "Human ear anatomy")),
            ChapterInfo("sc8_4", "Chemical Effects of Electric Current", 3, listOf("Electroplating", "Conductors & Insulators", "Electrolytes"))
        )
    )

    private fun createSocialScienceMiddle() = SubjectInfo(
        id = "sst_middle",
        name = "Social Science",
        iconName = "public",
        colorHex = "#C026D3",
        chapters = listOf(
            ChapterInfo("sst8_1", "Resources and Development", 3, listOf("Biotic & Abiotic", "Conservation of water & soil", "Minerals")),
            ChapterInfo("sst8_2", "The Indian Constitution & Secularism", 3, listOf("Preamble", "Fundamental Rights", "Directive Principles")),
            ChapterInfo("sst8_3", "The National Movement (1870s-1947)", 4, listOf("Non-Cooperation", "Civil Disobedience", "Quit India Movement"))
        )
    )

    private fun createEnglishMiddle() = SubjectInfo(
        id = "eng_middle",
        name = "English Grammar & Lit",
        iconName = "menu_book",
        colorHex = "#D97706",
        chapters = listOf(
            ChapterInfo("eng8_1", "Active and Passive Voice", 2, listOf("Tense transformation", "Imperative sentences")),
            ChapterInfo("eng8_2", "Direct and Indirect Speech", 3, listOf("Reporting verbs", "Punctuation change", "Pronoun shift")),
            ChapterInfo("eng8_3", "Formal Letter & Article Writing", 2, listOf("Format", "Tone & Coherence", "Editorial letters"))
        )
    )

    private fun createSecondaryMath() = SubjectInfo(
        id = "math_class10",
        name = "Mathematics (Class 10)",
        iconName = "calculate",
        colorHex = "#4338CA",
        chapters = listOf(
            ChapterInfo(
                "ch_real_num",
                "Real Numbers",
                3,
                listOf("Fundamental Theorem of Arithmetic", "Revisiting Irrational Numbers (√2, √3 proofs)", "Decimal expansions"),
                listOf("Confusing rational decimals with recurring irrationals", "Assuming √4 is irrational"),
                listOf("HCF(a,b) * LCM(a,b) = a * b")
            ),
            ChapterInfo(
                "ch_poly",
                "Polynomials",
                4,
                listOf("Geometric meaning of zeroes", "Relationship between zeroes and coefficients", "Quadratic factorisation"),
                listOf("Forgetting that degree gives maximum number of zeroes"),
                listOf("α + β = -b/a", "αβ = c/a")
            ),
            ChapterInfo(
                "ch_pair_lin",
                "Pair of Linear Equations",
                4,
                listOf("Graphical solution", "Substitution Method", "Elimination Method", "Consistency conditions"),
                listOf("Mixing up infinite solutions with parallel lines"),
                listOf("a1/a2 ≠ b1/b2 (Unique)", "a1/a2 = b1/b2 = c1/c2 (Infinite)", "a1/a2 = b1/b2 ≠ c1/c2 (No solution)")
            ),
            ChapterInfo(
                "ch_quad_eq",
                "Quadratic Equations",
                4,
                listOf("Standard form ax² + bx + c = 0", "Factorisation", "Quadratic Formula", "Nature of roots (Discriminant D)"),
                listOf("Ignoring ± in square root step", "Expanding (x+a)^2 as x^2 + a^2"),
                listOf("x = (-b ± √(b² - 4ac)) / (2a)", "D = b² - 4ac", "D > 0: Two distinct real roots", "D = 0: Equal roots")
            ),
            ChapterInfo(
                "ch_ap",
                "Arithmetic Progressions (AP)",
                4,
                listOf("nth term of an AP", "Sum of first n terms", "Word problems on AP"),
                listOf("Confusing n with a_n", "Off-by-one errors in count"),
                listOf("a_n = a + (n - 1)d", "S_n = (n/2)[2a + (n-1)d]", "S_n = (n/2)[a + l]")
            ),
            ChapterInfo(
                "ch_trig",
                "Introduction to Trigonometry",
                5,
                listOf("Trigonometric Ratios (sin, cos, tan, cot, sec, cosec)", "Values at 0°, 30°, 45°, 60°, 90°", "Trigonometric Identities"),
                listOf("Writing sin(A+B) as sin A + sin B", "Cancelling sin from sin θ / sin φ"),
                listOf("sin²θ + cos²θ = 1", "1 + tan²θ = sec²θ", "1 + cot²θ = cosec²θ")
            ),
            ChapterInfo(
                "ch_coord_geom",
                "Coordinate Geometry",
                3,
                listOf("Distance Formula", "Section Formula", "Midpoint Formula"),
                listOf("Mixing x and y coordinates in distance formula"),
                listOf("d = √((x2 - x1)² + (y2 - y1)²)", "(x, y) = ((m1x2 + m2x1)/(m1+m2), (m1y2 + m2y1)/(m1+m2))")
            )
        )
    )

    private fun createPhysicsSecondary() = SubjectInfo(
        id = "phy_class10",
        name = "Physics",
        iconName = "bolt",
        colorHex = "#0284C7",
        chapters = listOf(
            ChapterInfo(
                "ch_light",
                "Light - Reflection and Refraction",
                5,
                listOf("Spherical mirrors (Concave & Convex)", "Mirror formula and magnification", "Refractive Index & Snell's Law", "Lenses and Lens Formula", "Power of a Lens"),
                listOf("Cartesian sign convention mistakes", "Virtual image having real focal length"),
                listOf("1/v + 1/u = 1/f (Mirror)", "1/v - 1/u = 1/f (Lens)", "n21 = sin i / sin r", "P = 1/f(in meters) Dioptres")
            ),
            ChapterInfo(
                "ch_elec",
                "Electricity",
                5,
                listOf("Electric current and circuit", "Electric potential difference", "Ohm's Law (V = IR)", "Resistance and Resistivity", "Resistors in Series and Parallel", "Joule's Law of Heating", "Electric Power"),
                listOf("Confusing current division in series vs parallel", "Assuming resistance is zero in long wires"),
                listOf("V = IR", "R = ρ * (L/A)", "R_series = R1 + R2", "1/R_parallel = 1/R1 + 1/R2", "H = I²Rt", "P = VI = I²R = V²/R")
            ),
            ChapterInfo(
                "ch_mag_eff",
                "Magnetic Effects of Electric Current",
                4,
                listOf("Magnetic field and field lines", "Right-Hand Thumb Rule", "Solenoid magnetic field", "Fleming's Left-Hand Rule", "Electric Motor principle", "Electromagnetic Induction"),
                listOf("Applying left hand rule when right hand is required", "Field lines crossing each other"),
                listOf("F = B * I * L", "Field inside long solenoid B = μ₀ n I")
            )
        )
    )

    private fun createChemistrySecondary() = SubjectInfo(
        id = "chem_class10",
        name = "Chemistry",
        iconName = "science",
        colorHex = "#16A34A",
        chapters = listOf(
            ChapterInfo(
                "ch_chem_rxn",
                "Chemical Reactions and Equations",
                4,
                listOf("Balancing chemical equations", "Types: Combination, Decomposition, Displacement, Double Displacement", "Oxidation and Reduction (Redox)", "Corrosion and Rancidity"),
                listOf("Changing chemical formulas while balancing equations", "Confusing oxidizing agent with substance oxidized"),
                listOf("Law of Conservation of Mass: Mass of reactants = Mass of products")
            ),
            ChapterInfo(
                "ch_acids_bases",
                "Acids, Bases and Salts",
                4,
                listOf("Indicators (Litmus, Phenolphthalein, Olfactory)", "Chemical properties with metals and carbonates", "pH scale and its daily importance", "Salts: Bleaching powder, Baking soda, Washing soda, Plaster of Paris"),
                listOf("Adding water to concentrated acid instead of acid to water", "Neutral pH is always 7 at all temperatures"),
                listOf("pH = -log[H+]", "CaSO4 · 1/2 H2O (POP) + 1.5 H2O -> CaSO4 · 2H2O (Gypsum)")
            ),
            ChapterInfo(
                "ch_carbon",
                "Carbon and its Compounds",
                5,
                listOf("Covalent bonding in carbon", "Versatile nature (Catenation & Tetravalency)", "Homologous series", "Nomenclature of functional groups", "Combustion, Oxidation, Addition, Substitution", "Ethanol and Ethanoic Acid"),
                listOf("Writing pentavalent carbon", "Confusing alkanes with alkenes"),
                listOf("General Formula: Alkane CnH2n+2, Alkene CnH2n, Alkyne CnH2n-2")
            )
        )
    )

    private fun createBiologySecondary() = SubjectInfo(
        id = "bio_class10",
        name = "Biology",
        iconName = "psychology",
        colorHex = "#10B981",
        chapters = listOf(
            ChapterInfo(
                "ch_life_proc",
                "Life Processes",
                5,
                listOf("Autotrophic & Heterotrophic Nutrition", "Photosynthesis light & dark reactions", "Human Respiration (Aerobic vs Anaerobic)", "Human Circulatory System (Double Circulation)", "Human Excretory System (Nephron functioning)"),
                listOf("Believing plants only respire at night", "Assuming veins always carry deoxygenated blood (Pulmonary vein exception!)"),
                listOf("6CO2 + 6H2O -> C6H12O6 + 6O2", "Double Circulation: Heart pumps blood twice per cycle")
            ),
            ChapterInfo(
                "ch_ctrl_coord",
                "Control and Coordination",
                4,
                listOf("Structure of Neuron and Synapse", "Reflex Arc", "Human Brain (Forebrain, Midbrain, Hindbrain)", "Plant Hormones (Auxin, Gibberellin, Cytokinin, ABA)", "Endocrine Glands and Hormones"),
                listOf("Confusing voluntary with involuntary and reflex actions"),
                listOf("Neuron pathway: Dendrite -> Cell body -> Axon -> Synapse")
            ),
            ChapterInfo(
                "ch_heredity",
                "Heredity and Evolution",
                4,
                listOf("Mendel's Monohybrid and Dihybrid Crosses", "Laws of Inheritance", "Sex Determination in Humans (XX / XY)"),
                listOf("Believing mother determines the baby's sex", "Confusing phenotype ratio with genotype ratio"),
                listOf("Monohybrid Phenotypic Ratio: 3:1", "Genotypic Ratio: 1:2:1", "Dihybrid Phenotypic: 9:3:3:1")
            )
        )
    )

    private fun createSocialScienceSecondary() = SubjectInfo(
        id = "sst_class10",
        name = "Social Science",
        iconName = "account_balance",
        colorHex = "#9333EA",
        chapters = listOf(
            ChapterInfo("ch_hist_nat", "Rise of Nationalism in Europe", 4, listOf("French Revolution ideas", "Liberal Nationalism", "Unification of Germany and Italy")),
            ChapterInfo("ch_hist_ind", "Nationalism in India", 4, listOf("Rowlatt Act and Jallianwala Bagh", "Non-Cooperation Movement", "Salt March and Civil Disobedience")),
            ChapterInfo("ch_pol_power", "Power Sharing and Federalism", 3, listOf("Belgium and Sri Lanka models", "Union, State, and Concurrent Lists", "Decentralisation")),
            ChapterInfo("ch_econ_dev", "Economics: Money & Credit", 3, listOf("Barter system limitations", "Formal vs Informal credit sources", "Self-Help Groups"))
        )
    )

    private fun createEnglishSecondary() = SubjectInfo(
        id = "eng_class10",
        name = "English Language & Lit",
        iconName = "auto_stories",
        colorHex = "#E11D48",
        chapters = listOf(
            ChapterInfo("ch_lit_1", "Prose: A Letter to God & Nelson Mandela", 3, listOf("Character sketches", "Themes of faith and resilience", "Critical analysis")),
            ChapterInfo("ch_lit_2", "Poetry: Fire and Ice, The Road Not Taken", 2, listOf("Poetic devices (Metaphor, Alliteration, Irony)", "Central idea")),
            ChapterInfo("ch_grammar", "Analytical Paragraph & Grammar Editing", 3, listOf("Tenses & Modals", "Subject-Verb concord", "Reported Speech"))
        )
    )

    // Intermediate MPC
    private fun createInterMaths() = SubjectInfo(
        id = "inter_maths",
        name = "Intermediate Mathematics (1A / 1B / 2A / 2B)",
        iconName = "calculate",
        colorHex = "#4338CA",
        chapters = listOf(
            ChapterInfo(
                "im_calc",
                "Differential Calculus & Limits",
                6,
                listOf("Standard Limits (L'Hopital's Rule)", "Continuity & Differentiability", "Derivatives of composite, parametric & inverse functions", "Application of Derivatives: Tangents, Normals, Maxima & Minima"),
                listOf("Differentiating sin(x°) without converting to radians", "Applying L'Hopital when indeterminate form is not 0/0 or ∞/∞"),
                listOf("d/dx(x^n) = n*x^(n-1)", "d/dx(sin x) = cos x", "Chain Rule: dy/dx = (dy/du)*(du/dx)")
            ),
            ChapterInfo(
                "im_integ",
                "Integral Calculus",
                6,
                listOf("Indefinite Integration methods", "Integration by parts (ILATE rule)", "Definite Integrals properties", "Area under curves"),
                listOf("Forgetting + C in indefinite integrals", "Wrong substitution limit updates in definite integrals"),
                listOf("∫ u v dx = u ∫ v dx - ∫ [u' ∫ v dx] dx", "∫_0^a f(x)dx = ∫_0^a f(a - x)dx")
            ),
            ChapterInfo(
                "im_matrices",
                "Matrices and Determinants",
                5,
                listOf("Types of Matrices", "Determinant properties", "Cramer's Rule", "Matrix Inversion Method", "Rank of Matrix"),
                listOf("Multiplying matrices without matching inner dimensions (m x n * n x p)", "Assuming AB = BA in general"),
                listOf("A * A^(-1) = I", "A^(-1) = adj(A) / det(A)", "det(AB) = det(A) * det(B)")
            ),
            ChapterInfo(
                "im_vectors",
                "Vector Algebra & 3D Geometry",
                5,
                listOf("Scalar and Vector Dot & Cross Products", "Scalar Triple Product", "Direction Cosines and Ratios", "Lines and Planes in 3D"),
                listOf("Mixing dot product (scalar) with cross product (vector)", "Direction cosines sum: l² + m² + n² = 1"),
                listOf("a · b = |a||b|cos θ", "|a x b| = |a||b|sin θ", "l² + m² + n² = 1")
            ),
            ChapterInfo(
                "im_prob",
                "Probability & Random Variables",
                4,
                listOf("Conditional Probability", "Bayes' Theorem", "Binomial Distribution", "Mean & Variance"),
                listOf("Reversing P(A|B) and P(B|A) in Bayes' formula"),
                listOf("P(A|B) = P(A ∩ B) / P(B)", "P(Bi|A) = (P(Bi)P(A|Bi)) / Σ P(Bj)P(A|Bj)")
            )
        )
    )

    private fun createInterPhysics() = SubjectInfo(
        id = "inter_physics",
        name = "Intermediate Physics",
        iconName = "electric_bolt",
        colorHex = "#0284C7",
        chapters = listOf(
            ChapterInfo(
                "ip_mech",
                "Laws of Motion & Work-Energy-Power",
                6,
                listOf("Newton's 3 Laws with Free Body Diagrams (FBD)", "Friction on inclined planes", "Work-Energy Theorem", "Conservation of Linear Momentum", "Collisions (Elastic & Inelastic)"),
                listOf("Treating centrifugal force as an actual real force instead of pseudo force", "Confusing conservative and non-conservative forces"),
                listOf("F_net = m * a", "W = ΔK = Kf - Ki", "e = (v2 - v1)/(u1 - u2)")
            ),
            ChapterInfo(
                "ip_thermo",
                "Thermodynamics & Kinetic Theory",
                5,
                listOf("Zeroth, First & Second Laws of Thermodynamics", "Isothermal, Adiabatic, Isochoric, Isobaric processes", "Carnot Engine & Efficiency", "Molar Specific Heats (Cp - Cv = R)"),
                listOf("Sign convention of Work in Chemistry vs Physics: In Physics W = ∫ P dV (work done by gas is positive)"),
                listOf("ΔQ = ΔU + ΔW", "η = 1 - T_cold / T_hot", "Cp - Cv = R")
            ),
            ChapterInfo(
                "ip_elec_mag",
                "Electrostatics & Current Electricity",
                6,
                listOf("Coulomb's Law & Electric Dipole", "Gauss's Law & Applications", "Capacitance & Dielectrics", "Kirchhoff's Laws (KCL, KVL)", "Wheatstone Bridge"),
                listOf("Field inside a conductor at electrostatic equilibrium is zero", "Mixing series & parallel rules for capacitors vs resistors"),
                listOf("F = (1/4πε₀) * (q1q2 / r²)", "C = ε₀A / d", "C_parallel = C1 + C2", "1/C_series = 1/C1 + 1/C2")
            ),
            ChapterInfo(
                "ip_modern",
                "Modern Physics & Semiconductors",
                5,
                listOf("Photoelectric Effect (Einstein's Equation)", "Bohr's Atomic Model", "Nuclear Binding Energy & Half-Life", "p-n Junction Diode, Rectifiers, Logic Gates"),
                listOf("Thinking photon energy depends on light intensity instead of frequency"),
                listOf("E = hν = hc / λ", "hν = Φ + KE_max", "r_n = 0.529 n² / Z Å")
            )
        )
    )

    private fun createInterChemistry() = SubjectInfo(
        id = "inter_chem",
        name = "Intermediate Chemistry",
        iconName = "science",
        colorHex = "#16A34A",
        chapters = listOf(
            ChapterInfo(
                "ic_organic",
                "Organic Chemistry: Reaction Mechanisms",
                6,
                listOf("Inductive, Resonance & Hyperconjugation effects", "SN1 vs SN2 Nucleophilic Substitution", "Electrophilic Addition to Alkenes (Markovnikov's Rule)", "Aldehydes, Ketones & Carboxylic Acids", "Name Reactions: Aldol, Cannizzaro, Wurtz, Kolbe"),
                listOf("SN1 proceeds via carbocation (racemisation), SN2 is bimolecular with Walden inversion", "Markovnikov rule: electrophile H+ attacks carbon with more hydrogens"),
                listOf("Carbocation stability: 3° > 2° > 1° > methyl", "Free radical stability follows same trend")
            ),
            ChapterInfo(
                "ic_physical",
                "Physical Chemistry: Equilibrium & Kinetics",
                5,
                listOf("Chemical Equilibrium (Kc & Kp)", "Le Chatelier's Principle", "Ionic Equilibrium (pH, Buffer solutions, Solubility product Ksp)", "Chemical Kinetics (Rate Law, Order, Arrhenius Equation)"),
                listOf("Catalyst changes rate but DOES NOT shift position of equilibrium Kc"),
                listOf("Kp = Kc (RT)^Δn", "k = A e^(-Ea / RT)", "Henderson Equation: pH = pKa + log([Salt]/[Acid])")
            ),
            ChapterInfo(
                "ic_inorganic",
                "Inorganic Chemistry & Coordination Compounds",
                5,
                listOf("Periodic Trends (IE, EA, Electronegativity)", "Chemical Bonding (Hybridization, VSEPR, MOT)", "d and f Block Elements", "Werner's Theory, Valence Bond Theory (VBT) and Crystal Field Theory (CFT)"),
                listOf("Paramagnetic substances have unpaired electrons in MOT orbital diagram"),
                listOf("Bond Order = 1/2 (Nb - Na)", "Magnetic Moment μ = √(n(n+2)) BM")
            )
        )
    )

    private fun createInterBiology() = SubjectInfo(
        id = "inter_bio",
        name = "Intermediate Biology (Botany & Zoology)",
        iconName = "psychology",
        colorHex = "#059669",
        chapters = listOf(
            ChapterInfo("ib_cell", "Cell Biology & Genetics", 5, listOf("Mitosis vs Meiosis stages", "DNA Replication enzymes (Helicase, Polymerase)", "Transcription & Translation", "Mendelian & Chromosomal disorders")),
            ChapterInfo("ib_plant_phys", "Plant Physiology", 5, listOf("Photosynthesis C3 vs C4 cycle", "CAM plants adaptation", "Respiration in Plants (Glycolysis, Krebs Cycle)", "Plant Growth Regulators")),
            ChapterInfo("ib_human_phys", "Human Physiology", 6, listOf("Neural conduction (Resting & Action potentials)", "Cardiac cycle and ECG waves", "Sliding Filament Theory of muscle contraction", "Kidney counter-current mechanism")),
            ChapterInfo("ib_biotech", "Biotechnology Principles & Processes", 4, listOf("Restriction enzymes & Ligases", "Plasmids (pBR322)", "PCR (Denaturation, Annealing, Extension)", "Transgenic animals & Bt Cotton"))
        )
    )

    private fun createInterEconomics() = SubjectInfo(
        id = "inter_econ",
        name = "Intermediate Economics",
        iconName = "trending_up",
        colorHex = "#2563EB",
        chapters = listOf(
            ChapterInfo("ie_micro", "Microeconomics: Demand & Elasticity", 4, listOf("Law of Demand & Exceptions", "Price Elasticity of Demand (Formulae)", "Indifference Curve analysis", "Production functions & Cost curves")),
            ChapterInfo("ie_macro", "Macroeconomics: National Income & Money", 5, listOf("GDP, GNP, NNP calculations", "Circular flow of income", "Central Bank (RBI) monetary policies: Repo rate, CRR, SLR", "Fiscal deficit and government budget"))
        )
    )

    private fun createInterCommerce() = SubjectInfo(
        id = "inter_comm",
        name = "Accountancy & Commerce",
        iconName = "receipt_long",
        colorHex = "#D97706",
        chapters = listOf(
            ChapterInfo("ic_acc", "Financial Accounting & Trial Balance", 5, listOf("Double Entry Bookkeeping rules", "Journal entries & Ledger posting", "Trial Balance reconciliation", "Depreciation methods (Straight line vs WDV)")),
            ChapterInfo("ic_bus", "Business Organisation & Management", 4, listOf("Sole Proprietorship vs Partnership vs Company", "Principles of Management (Fayol & Taylor)", "Sources of Business Finance (Equity, Debentures)"))
        )
    )

    private fun createInterCivics() = SubjectInfo(
        id = "inter_civ",
        name = "Civics & Political Science",
        iconName = "gavel",
        colorHex = "#7C3AED",
        chapters = listOf(
            ChapterInfo("ic_pol", "Political Concepts & Indian Constitution", 4, listOf("State, Sovereignty and Liberty", "Fundamental Rights & Duties", "Parliamentary Democracy & Judiciary", "Election Commission of India"))
        )
    )

    private fun createInterEnglish() = SubjectInfo(
        id = "inter_eng",
        name = "General English & Communication",
        iconName = "school",
        colorHex = "#BE123C",
        chapters = listOf(
            ChapterInfo("ie_lit", "Prescribed Literature & Short Fiction", 3, listOf("Critical analysis of prose and poetry", "Theme and stylistic devices")),
            ChapterInfo("ie_comp", "Advanced Writing & Grammar", 3, listOf("Note-making and Summarising", "Curriculum Vitae / Resume writing", "Correction of Sentences & Idioms"))
        )
    )

    private fun createDataStructuresAndAlgorithms() = SubjectInfo(
        id = "cs_dsa",
        name = "Data Structures & Algorithms",
        iconName = "hub",
        colorHex = "#4338CA",
        chapters = listOf(
            ChapterInfo("dsa_linear", "Arrays, Linked Lists, Stacks & Queues", 6, listOf("Two-Pointer techniques & Sliding Window", "Singly & Doubly Linked Lists", "Monotonic Stack & Queue applications", "Infix to Postfix conversion")),
            ChapterInfo("dsa_trees", "Binary Trees & BST", 5, listOf("Preorder, Inorder, Postorder Traversals (Recursive & Iterative)", "Lowest Common Ancestor (LCA)", "Binary Search Tree operations & Balancing (AVL / Red-Black)")),
            ChapterInfo("dsa_graphs", "Graph Algorithms", 6, listOf("BFS and DFS traversals", "Dijkstra's Shortest Path & Bellman-Ford", "Minimum Spanning Tree (Prim's & Kruskal's)", "Topological Sort & Cycle Detection")),
            ChapterInfo("dsa_dp", "Dynamic Programming & Recursion", 6, listOf("0/1 Knapsack & Unbounded variants", "Longest Common Subsequence (LCS)", "Matrix Chain Multiplication (MCM)", "State Memoization vs Tabulation"))
        )
    )

    private fun createDatabaseManagementSystems() = SubjectInfo(
        id = "cs_dbms",
        name = "Database Management Systems (DBMS)",
        iconName = "storage",
        colorHex = "#0D9488",
        chapters = listOf(
            ChapterInfo("dbms_relational", "Relational Models & SQL", 5, listOf("ER Diagrams & Relational Mapping", "Complex SQL Joins (INNER, LEFT, RIGHT, FULL)", "Subqueries, Aggregate Functions & Group By", "Triggers and Stored Procedures")),
            ChapterInfo("dbms_normal", "Normalization & Dependency", 5, listOf("Functional Dependencies & Armstrong Axioms", "1NF, 2NF, 3NF and BCNF Decompositions", "Lossless Join and Dependency Preserving Decomposition")),
            ChapterInfo("dbms_acid", "Transactions & Concurrency Control", 5, listOf("ACID Properties and State Transition", "Two-Phase Locking (2PL) Protocol", "Deadlock Detection & Prevention", "Serializability & Recoverability"))
        )
    )

    private fun createOperatingSystems() = SubjectInfo(
        id = "cs_os",
        name = "Operating Systems",
        iconName = "memory",
        colorHex = "#D97706",
        chapters = listOf(
            ChapterInfo("os_proc", "Process Management & CPU Scheduling", 5, listOf("Process Control Block (PCB) & Context Switching", "FCFS, SJF, Round Robin, Priority Scheduling", "Inter-Process Communication (Pipes, Shared Memory)")),
            ChapterInfo("os_sync", "Synchronization & Deadlocks", 5, listOf("Critical Section Problem & Semaphores", "Dining Philosophers & Producer-Consumer", "Banker's Algorithm for Deadlock Avoidance")),
            ChapterInfo("os_mem", "Memory Management & Virtual Memory", 5, listOf("Paging, Segmentation and TLB", "Page Replacement (FIFO, LRU, Optimal)", "Thrashing and Working Set Model"))
        )
    )

    private fun createComputerNetworks() = SubjectInfo(
        id = "cs_cn",
        name = "Computer Networks",
        iconName = "lan",
        colorHex = "#0284C7",
        chapters = listOf(
            ChapterInfo("cn_models", "OSI & TCP/IP Protocol Suite", 4, listOf("7 Layers of OSI vs 4 Layers of TCP/IP", "Data Link: Framing, CRC Error Detection & Stop-and-Wait", "Sliding Window Protocols (Go-Back-N, Selective Repeat)")),
            ChapterInfo("cn_routing", "Network Layer & IP Addressing", 5, listOf("IPv4 vs IPv6 Subnetting & CIDR", "Routing Algorithms: Distance Vector (RIP) vs Link State (OSPF)", "BGP and Congestion Control (Leaky Bucket, Token Bucket)")),
            ChapterInfo("cn_trans", "Transport & Application Layer", 5, listOf("TCP 3-Way Handshake, Flow Control & Congestion Window", "UDP vs TCP Socket programming", "DNS, HTTP/1.1 vs HTTP/2 vs HTTP/3, SSL/TLS handshake"))
        )
    )

    private fun createObjectOrientedProgramming() = SubjectInfo(
        id = "cs_oops",
        name = "Object-Oriented Programming (Java/C++)",
        iconName = "code",
        colorHex = "#9333EA",
        chapters = listOf(
            ChapterInfo("oops_core", "Core OOP Principles", 5, listOf("Encapsulation, Data Hiding & Access Modifiers", "Inheritance (Single, Multilevel, Multiple through Interfaces)", "Polymorphism: Method Overloading vs Overriding", "Abstract Classes vs Interfaces")),
            ChapterInfo("oops_collections", "Exception Handling & Collections Framework", 5, listOf("Try-Catch-Finally, Throw and Throws", "Java Collections: List, Set, Map, HashMap, TreeMap", "Generics, Multithreading & Synchronization in Java"))
        )
    )

    private fun createArtificialIntelligence() = SubjectInfo(
        id = "cs_ai",
        name = "Artificial Intelligence & Machine Learning",
        iconName = "psychology",
        colorHex = "#E11D48",
        chapters = listOf(
            ChapterInfo("ai_search", "Search Algorithms & Heuristics", 5, listOf("Uninformed Search: BFS, DFS, Uniform Cost", "Informed Search: A* Search, Greedy Best-First", "Adversarial Search: Minimax & Alpha-Beta Pruning")),
            ChapterInfo("ai_ml", "Supervised & Unsupervised Learning", 6, listOf("Linear & Logistic Regression", "Decision Trees & Random Forests", "K-Means Clustering & PCA dimensionality reduction", "Neural Networks & Backpropagation fundamentals"))
        )
    )

    private fun createPythonProgramming() = SubjectInfo(
        id = "cs_py",
        name = "Python for Data Science",
        iconName = "terminal",
        colorHex = "#16A34A",
        chapters = listOf(
            ChapterInfo("py_core", "Python Foundations & Data Structures", 4, listOf("Lists, Tuples, Dictionaries, Sets & Comprehensions", "Lambdas, Map, Filter, Reduce", "File I/O, Generators and Decorators")),
            ChapterInfo("py_libs", "NumPy, Pandas & Visualization", 5, listOf("NumPy vectorization & Matrix Operations", "Pandas DataFrames, Cleaning, GroupBy & Merging", "Matplotlib & Seaborn statistical plotting"))
        )
    )

    private fun createBusinessAndFinance() = SubjectInfo(
        id = "mgmt_fin",
        name = "Business Management & Corporate Finance",
        iconName = "trending_up",
        colorHex = "#B45309",
        chapters = listOf(
            ChapterInfo("mgmt_core", "Principles of Corporate Management", 4, listOf("Planning, Organizing, Leading, Controlling (POLC)", "Strategic Management: SWOT & Porter's Five Forces", "Marketing Mix: The 4 Ps & Digital Marketing")),
            ChapterInfo("mgmt_fin_analysis", "Financial Statement Analysis & Capital Budgeting", 5, listOf("Ratio Analysis (Liquidity, Solvency, Profitability)", "Cash Flow Statement (Direct vs Indirect)", "Net Present Value (NPV), IRR & Payback Period"))
        )
    )
}
